package com.mvp.mvp.service.impl;

import com.mvp.mvp.model.NoMoneyException;
import com.mvp.mvp.model.NoRowException;
import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.model.request.BuyRequest;
import com.mvp.mvp.model.request.ProductRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.model.response.BuyProductResponse;
import com.mvp.mvp.model.response.ProductResponse;
import com.mvp.mvp.model.security.UserDetailsImpl;
import com.mvp.mvp.repository.ProductRepository;
import com.mvp.mvp.repository.UserRepository;
import com.mvp.mvp.service.ProductService;
import com.mvp.mvp.util.ProductUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ValidationException;
import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private TransactionHandler transactionHandler;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private UserRepository userRepository;
    @Value("${coins}")
    private List<Long> coins;

    @Override
    public BuyProductResponse buyProducts(BuyRequest buyRequest) {
        Product product = productRepository.finByName(buyRequest.getProductName()).orElseThrow(() -> new NoRowException("There is no row with name" + buyRequest.getProductName()));
        User user = transactionHandler.runInTransaction(this::fetchUser);
        Long totalAmount = calculatePrice(buyRequest, product);
        if (product.getAmountAvailable() < buyRequest.getAmount() || totalAmount > user.getDeposit()) {
            throw new NoMoneyException("Deposit lower then amount or there is not enough items on stock!");
        }
        userRepository.subtractDepositFromUserById(totalAmount, user.getId());
        productRepository.updateProductByAmount(buyRequest.getAmount(), product.getProductId());
        user.setDeposit(user.getDeposit() - totalAmount);
        Map<Long, Long> coins = calculateCoins(user);
        return BuyProductResponse.constructBuyProductResponse(totalAmount, product.getProductName(), coins);
    }

    @Override
    public Long save(ProductRequest productRequest) {
        User user = transactionHandler.runInTransaction(this::fetchUser);
        Product product = productRequest.createProductFromRequest(user);
        return productRepository.save(product).getProductId();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    @PreAuthorize("@OwnerHandler.checkIfUserOwns(#product)")
    public ProductResponse updateProduct(Product product, UpdateRequest productRequest) {
        ProductUtil.updateProduct(productRequest, product);
        product = productRepository.save(product);
        return ProductResponse.createProductResponse(product);
    }


    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    @PreAuthorize("@OwnerHandler.checkIfUserOwns(#product)")
    public void delete(Product product) {
        productRepository.deleteById(product.getProductId());
    }

    @Override
    public ProductResponse getById(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NoRowException("No such object found with product id " + id));
        return ProductResponse.createProductResponse(product);
    }

    private Long calculatePrice(BuyRequest buyRequest, Product product) {
        return buyRequest.getAmount() * product.getCost();
    }

    private Map<Long, Long> calculateCoins(User user) {
        Map<Long, Long> mapOfCoins = new HashMap<>();
        Iterator<Long> iterator = coins.iterator();
        calculateForEachCoinAndPut(user.getDeposit(), iterator.next(), mapOfCoins, iterator);
        return mapOfCoins;
    }

    private void calculateForEachCoinAndPut(Long amount, Long coin, Map<Long, Long> mapOfCoins, Iterator<Long> coins) {
        mapOfCoins.put(coin, amount / coin);
        if (!coins.hasNext()) {
            return;
        }
        calculateForEachCoinAndPut(amount % coin, coins.next(), mapOfCoins, coins);
    }

    private User fetchUser() {
        Optional<User> optionalUser = userRepository.findById(
                ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId());
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("SecurityContext holds invalid user!"));
    }

}
