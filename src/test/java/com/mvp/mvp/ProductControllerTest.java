package com.mvp.mvp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.Role;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.model.request.BuyRequest;
import com.mvp.mvp.model.request.ProductRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.repository.ProductRepository;
import com.mvp.mvp.repository.RoleRepository;
import com.mvp.mvp.repository.UserRepository;
import com.mvp.mvp.security.JwtProperties;
import com.mvp.mvp.security.JwtProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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
    private MockMvc mockMvc;

    @Autowired
    public ProductControllerTest(WebApplicationContext context, JwtProvider jwtProvider, JwtProperties jwtProperties, UserRepository userRepository, RoleRepository roleRepository, ProductRepository productRepository) {
        this.context = context;
        this.jwtProvider = jwtProvider;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.productRepository = productRepository;
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

    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity())
                .build();
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
        setup();
        insertUser();
        String token = this.jwtProvider.createToken(USERNAME);
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);

        //when then
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/product/buy").content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(jwtProperties.getHeaderName(),
                                jwtProperties.getStartsWith() + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }


    @Test
    public void buyProductNoRole() throws Exception {
        //given
        setup();
        insertUser();
        User user = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("f").withDeposit(500L).build();
        String token = this.jwtProvider.createToken("f");
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);

        //when then
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/product/buy").content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(jwtProperties.getHeaderName(),
                                jwtProperties.getStartsWith() + token))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void buyProductValidation() throws Exception {
        //given
        setup();
        insertUser();
        User user = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("f").withDeposit(500L).build();
        String token = this.jwtProvider.createToken("f");
        BuyRequest buyRequest = BuyRequest.BuyRequestBuilder.aBuyRequest().withProductName(PRODUCT_NAME).withAmount(-2L).build();
        String object = objectMapper.writeValueAsString(buyRequest);

        //when then
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/product/buy").content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(jwtProperties.getHeaderName(),
                                jwtProperties.getStartsWith() + token))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void updateProduct() throws Exception {
        //given
        setup();
        User user = insertUser();
        String token = this.jwtProvider.createToken(USERNAME);
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user.getProducts().get(0).getProductId()).withProductName("new").withCost(5L).withAmountAvailable(4L).build();
        String object = objectMapper.writeValueAsString(productRequest);

        //when then
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/product/update").content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(jwtProperties.getHeaderName(),
                                jwtProperties.getStartsWith() + token))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void updateProductNotOwner() throws Exception {
        //given
        setup();
        User user = insertUser();
        User user2 = User.UserBuilder.anUser().withPassword("f".toCharArray()).withUsername("F").withDeposit(500L).build();
        userRepository.save(user2);
        String token = this.jwtProvider.createToken("f");
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user.getProducts().get(0).getProductId()).withProductName("new").withCost(5L).withAmountAvailable(4L).build();
        String object = objectMapper.writeValueAsString(productRequest);

        //when then
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/product/update").content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(jwtProperties.getHeaderName(),
                                jwtProperties.getStartsWith() + token))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }

    @Test
    public void updateProductTestCustomValidation() throws Exception {
        //given
        setup();
        User user = insertUser();
        String token = this.jwtProvider.createToken(USERNAME);
        UpdateRequest productRequest = UpdateRequest.updateRequestBuilder.aupdateRequest().withProductId(user.getProducts().get(0).getProductId()).withProductName("new").withCost(3L).withAmountAvailable(4L).build();
        String object = objectMapper.writeValueAsString(productRequest);

        //when then
        this.mockMvc
                .perform(MockMvcRequestBuilders.put("/product/update").content(object)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(jwtProperties.getHeaderName(),
                                jwtProperties.getStartsWith() + token))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}
