package com.mvp.mvp;

import com.mvp.mvp.model.NoMoneyException;
import com.mvp.mvp.model.NoRowException;
import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.ProductResolverEnum;
import com.mvp.mvp.model.entity.Role;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.model.request.BuyRequest;
import com.mvp.mvp.model.request.ProductRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.model.response.BuyProductResponse;
import com.mvp.mvp.model.response.ProductResponse;
import com.mvp.mvp.repository.ProductRepository;
import com.mvp.mvp.repository.RoleRepository;
import com.mvp.mvp.repository.UserRepository;
import com.mvp.mvp.security.JwtProvider;
import com.mvp.mvp.service.ProductResolverService;
import com.mvp.mvp.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductServiceTests {

    private static final String USERNAME = "a";
    private static final String PRODUCT_NAME = "juice";
    private static final String ROLE_NAME = "ROLE_BUYER";
    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withPassword("inmemory")
            .withUsername("inmemory");
    private ProductService productService;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private JwtProvider jwtProvider;
    private RoleRepository roleRepository;
    private ProductResolverService productResolverService;

    @Autowired
    public ProductServiceTests(ProductService productService, UserRepository userRepository, ProductRepository productRepository, JwtProvider jwtProvider, RoleRepository roleRepository, ProductResolverService productResolverService) {
        this.productService = productService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;
        this.productResolverService = productResolverService;
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    public void prepareForTesting() {
    }

    @BeforeEach
    private void deleteEverything() {
        productRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private User insertUser(String name) {
        Role role = new Role();
        role.setName(ROLE_NAME);
        role = roleRepository.save(role);
        User user = User.UserBuilder.anUser().withPassword("a".toCharArray()).withUsername(USERNAME).withRoles(List.of(role)).withDeposit(500L).build();
        role.setUsers(List.of(user));
        user = userRepository.save(user);
        createProduct(user, name, 100L);
        SecurityContextHolder.getContext().setAuthentication(jwtProvider.getAuthenticationByUsername(USERNAME));
        return user;
    }

    private void createProduct(User user, String name, Long amount) {
        Product product = Product.ProductBuilder.aProduct().withProductName(name).withCost(amount).withAmountAvailable(100L).withUser(user).build();
        product = productRepository.save(product);
        user.getProducts().add(product);
    }

    @Test
    public void saveProduct() {
        //given
        insertUser("test");
        ProductRequest productRequest = ProductRequest.ProductRequestBuilder.aProductRequest().withProductName("name").withCost(10L).withAmountAvailable(40L).build();
        //when
        productService.save(productRequest);
        Product product = productRepository.finByName("name").get();
        //then
        Assertions.assertEquals(product.getAmountAvailable(), 40L);
        Assertions.assertEquals(product.getCost(), 10L);
    }

    @Test
    public void buyProduct() {
        //given
        User user = insertUser("test");
        createProduct(user, "reset", 100L);
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName("reset").withAmount(2L).build();

        //when
        BuyProductResponse buyProductResponse = productService.buyProducts(buyRequest);
        user = userRepository.findByUsername(USERNAME).get();
        Product product = ((List<Product>) productRepository.findAll()).stream().filter(n -> n.getProductName().equals("reset")).findFirst().get();

        //then
        Assertions.assertEquals(product.getAmountAvailable(), 98L);
        Assertions.assertEquals(product.getCost(), 100L);
        Assertions.assertEquals(user.getDeposit(), 300L);
        Assertions.assertEquals(buyProductResponse.getCoins().get(100L), 3);
    }

    @Test
    public void buyProductChekChange() {
        //given
        User user = insertUser("test");
        createProduct(user, "reset", 15L);
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName("reset").withAmount(1L).build();

        //when
        BuyProductResponse buyProductResponse = productService.buyProducts(buyRequest);
        user = userRepository.findByUsername(USERNAME).get();
        Product product = ((List<Product>) productRepository.findAll()).stream().filter(n -> n.getProductName().equals("reset")).findFirst().get();

        //then
        Assertions.assertEquals(product.getAmountAvailable(), 99L);
        Assertions.assertEquals(product.getCost(), 15L);
        Assertions.assertEquals(user.getDeposit(), 485L);
        Assertions.assertEquals(buyProductResponse.getCoins().get(100L), 4);
        Assertions.assertEquals(buyProductResponse.getCoins().get(50L), 1);
        Assertions.assertEquals(buyProductResponse.getCoins().get(20L), 1);
        Assertions.assertEquals(buyProductResponse.getCoins().get(10L), 1);
        Assertions.assertEquals(buyProductResponse.getCoins().get(5L), 1);
    }


    @Test
    public void buyProductFailNoMoney() {
        //given
        User user = insertUser("test");
        createProduct(user, "reset", 100L);
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName("reset").withAmount(6L).build();

        //when then
        NoMoneyException thrown = Assertions.assertThrows(
                NoMoneyException.class,
                () -> productService.buyProducts(buyRequest));
        Assertions.assertNotNull(thrown);
    }

    @Test
    public void updateProduct() {
        //given
        User user = insertUser("test");
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user.getProducts().get(0).getProductId())
                .withAmountAvailable(2L).withCost(30L).withProductName("new").build();
        //when
        productResolverService.resolveCall(productRequest, productRequest.getProductId(), ProductResolverEnum.UPDATE);
        Product product = productRepository.findById(productRequest.getProductId()).get();

        //then
        Assertions.assertEquals(product.getProductName(), "new");
        Assertions.assertEquals(product.getCost(), 30L);
        Assertions.assertEquals(product.getAmountAvailable(), 2L);
    }

    @Test
    public void updateProductFailNotOwner() {
        //given
        insertUser("test");
        User user2 = User.UserBuilder.anUser().withUsername("f").withPassword("f".toCharArray()).build();
        userRepository.save(user2);
        createProduct(user2, "reset", 100L);
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user2.getProducts().get(0).getProductId())
                .withAmountAvailable(2L).withCost(30L).withProductName("new").build();

        //when then
        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> productResolverService.resolveCall(productRequest, productRequest.getProductId(), ProductResolverEnum.UPDATE));
    }

    @Test
    public void deleteProduct() {
        //given
        User user = insertUser("test");
        ProductRequest productRequest = ProductRequest.ProductRequestBuilder.aProductRequest().withProductId(user.getProducts().get(0).getProductId())
                .withAmountAvailable(2L).withCost(30L).withProductName("new").build();
        //when
        productResolverService.resolveCall(productRequest.getProductId(), productRequest.getProductId(), ProductResolverEnum.DELETE);

        //then
        Assertions.assertTrue(productRepository.findById(productRequest.getProductId()).isEmpty());
        Assertions.assertFalse(((List<User>)userRepository.findAll()).isEmpty());
    }

    @Test
    public void deleteProductFailNotOwner() {
        //given
        insertUser("test");
        User user2 = User.UserBuilder.anUser().withUsername("f").withPassword("f".toCharArray()).build();
        userRepository.save(user2);
        createProduct(user2, "reset", 100L);

        ProductRequest productRequest = ProductRequest.ProductRequestBuilder.aProductRequest().withProductId(user2.getProducts().get(0).getProductId())
                .withAmountAvailable(2L).withCost(30L).withProductName("new").build();

        //when then
        Assertions.assertThrows(
                AccessDeniedException.class,
                () -> productResolverService.resolveCall(productRequest.getProductId(), productRequest.getProductId(), ProductResolverEnum.DELETE));
    }

    @Test
    public void getProduct() {
        //given
        User user = insertUser("test");
        Product product = user.getProducts().get(0);
        //when
        ProductResponse productResponse = productService.getById(product.getProductId());

        //then
        Assertions.assertNotNull(productResponse);
        Assertions.assertEquals(product.getAmountAvailable(), productResponse.getAmountAvailable());
        Assertions.assertEquals(product.getCost(), productResponse.getCost());
        Assertions.assertEquals(product.getProductName(), productResponse.getProductName());
    }


    @Test
    public void getProductNoProduct() {
        //given
        User user = insertUser("test");
        Product product = user.getProducts().get(0);

        //when then
        Assertions.assertThrows(
                NoRowException.class,
                () -> productService.getById(product.getProductId() + 11));
    }
}
