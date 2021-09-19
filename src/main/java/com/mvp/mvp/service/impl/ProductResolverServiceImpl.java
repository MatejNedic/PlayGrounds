package com.mvp.mvp.service.impl;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.ProductResolverEnum;
import com.mvp.mvp.model.request.ProductRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.repository.ProductRepository;
import com.mvp.mvp.service.ProductResolverService;
import com.mvp.mvp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
public class ProductResolverServiceImpl implements ProductResolverService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductService productService;

    public <T, X> X resolveCall(T request, Long id, ProductResolverEnum productResolverEnum) {
        Product product = productRepository.findById(id).orElseThrow(() -> new NoSuchElementException("there is no row with id " + id));
        if (productResolverEnum.equals(ProductResolverEnum.UPDATE)) {
            return (X) productService.updateProduct(product, (UpdateRequest) request);
        } else {
            productService.delete(product);
        }
        return null;
    }


}
