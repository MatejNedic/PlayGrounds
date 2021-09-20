package com.mvp.mvp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.Role;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.model.request.BuyRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.repository.ProductRepository;
import com.mvp.mvp.repository.RoleRepository;
import com.mvp.mvp.repository.UserRepository;
import com.mvp.mvp.security.JwtProperties;
import com.mvp.mvp.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import springfox.documentation.spring.web.json.Json;

import java.util.List;

@AutoConfigureWebTestClient
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProductControllerTest {


    private static final String USERNAME = "a";
    private static final String PRODUCT_NAME = "juice";
    private static final String ROLE_NAME = "ROLE_BUYER";
    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withPassword("inmemory")
            .withUsername("inmemory");
    private ObjectMapper objectMapper = new ObjectMapper();
    private WebApplicationContext context;
    private JwtProvider jwtProvider;
    private JwtProperties jwtProperties;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private ProductRepository productRepository;
    private WebTestClient webTestClient;

    @Autowired
    public ProductControllerTest(WebApplicationContext context, JwtProvider jwtProvider, JwtProperties jwtProperties, UserRepository userRepository, RoleRepository roleRepository, ProductRepository productRepository, WebTestClient webTestClient) {
        this.context = context;
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
        this.webTestClient = webTestClient;
    }

    @BeforeEach
    private void deleteEverything() {
        productRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    private User insertUser() {
        Role role = new Role();
        role.setName(ROLE_NAME);
        role = roleRepository.save(role);
        User user = User.UserBuilder.anUser().withPassword("a".toCharArray()).withUsername(USERNAME).withDeposit(500L).withRoles(List.of(role)).build();
        role.setUsers(List.of(user));
        user = userRepository.save(user);
        createProduct(user);
        return user;
    }

    private void createProduct(User user) {
        Product product = Product.ProductBuilder.aProduct().withProductName(PRODUCT_NAME).withCost(100L).withAmountAvailable(100L).withUser(user).build();
        product = productRepository.save(product);
        user.getProducts().add(product);
    }


    @Test
    public void buyProduct() throws Exception {
        //given
        insertUser();
        String token = this.jwtProvider.createToken(USERNAME);
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);
        Json json = new Json(object);


        //when then
        this.webTestClient.post().uri("/product/buy").bodyValue(json).header(jwtProperties.getHeaderName(),
                jwtProperties.getStartsWith() + token).exchange().expectStatus().isOk();
    }


    @Test
    public void buyProductNoRole() throws Exception {
        //given

        insertUser();
        User user = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("f").withDeposit(500L).build();
        String token = this.jwtProvider.createToken("f");
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);
        Json json = new Json(object);


        //when then
        this.webTestClient.post().uri("/product/buy").bodyValue(json).header(jwtProperties.getHeaderName(),
                jwtProperties.getStartsWith() + token).exchange().expectStatus().is4xxClientError();
    }

    @Test
    public void buyProductNoToken() throws Exception {
        //given

        insertUser();
        User user = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("f").withDeposit(500L).build();
        String token = this.jwtProvider.createToken("f");
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);
        Json json = new Json(object);

        //when then
        this.webTestClient.post().uri("/product/buy").bodyValue(json).exchange().expectStatus().is4xxClientError();
    }

    @Test
    public void buyProductValidation() throws Exception {
        //given

        insertUser();
        User user = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("f").withDeposit(500L).build();
        String token = this.jwtProvider.createToken("f");
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(-2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);
        Json json = new Json(object);

        //when then
        this.webTestClient.post().uri("/product/buy").bodyValue(json).header(jwtProperties.getHeaderName(),
                jwtProperties.getStartsWith() + token).exchange().expectStatus().is4xxClientError();
    }

    @Test
    public void updateProduct() throws Exception {
        //given

        User user = insertUser();
        String token = this.jwtProvider.createToken(USERNAME);
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user.getProducts().get(0).getProductId()).withProductName("new").withCost(5L).withAmountAvailable(4L).build();
        String object = objectMapper.writeValueAsString(productRequest);
        Json json = new Json(object);

        //when then
        this.webTestClient.put().uri("/product/update").bodyValue(json).accept(MediaType.APPLICATION_JSON).header(jwtProperties.getHeaderName(),
                jwtProperties.getStartsWith() + token).exchange().expectStatus().isOk();
    }

    @Test
    public void updateProductNotOwner() throws Exception {
        //given
        User user = insertUser();
        User user2 = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("F").withDeposit(500L).build();
        userRepository.save(user2);
        String token = this.jwtProvider.createToken("f");
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user.getProducts().get(0).getProductId()).withProductName("new").withCost(5L).withAmountAvailable(4L).build();
        String object = objectMapper.writeValueAsString(productRequest);
        Json json = new Json(object);

        //when then
        this.webTestClient.put().uri("/product/update").bodyValue(json).accept(MediaType.APPLICATION_JSON).header(jwtProperties.getHeaderName(),
                jwtProperties.getStartsWith() + token).exchange().expectStatus().is4xxClientError();
    }

}
