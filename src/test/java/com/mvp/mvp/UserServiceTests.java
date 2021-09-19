package com.mvp.mvp;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.Role;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.model.request.DepositRequest;
import com.mvp.mvp.model.request.RegisterRequest;
import com.mvp.mvp.model.response.UserResponse;
import com.mvp.mvp.repository.ProductRepository;
import com.mvp.mvp.repository.RoleRepository;
import com.mvp.mvp.repository.UserRepository;
import com.mvp.mvp.security.JwtProvider;
import com.mvp.mvp.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserServiceTests {

    private static final String USERNAME = "a";
    private static final String PRODUCT_NAME = "juice";
    private static final String ROLE_NAME = "ROLE_BUYER";
    @Container
    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer()
            .withPassword("inmemory")
            .withUsername("inmemory");
    private UserService userService;
    private UserRepository userRepository;
    private ProductRepository productRepository;
    private JwtProvider jwtProvider;
    private RoleRepository roleRepository;

    @Autowired
    public UserServiceTests(UserService userService, UserRepository userRepository, ProductRepository productRepository, JwtProvider jwtProvider, RoleRepository roleRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.jwtProvider = jwtProvider;
        this.roleRepository = roleRepository;
    }

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
    }

    @BeforeEach
    private void deleteEverything() {
        productRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    private User insertUser() {
        Role role = new Role();
        role.setName(ROLE_NAME);
        role = roleRepository.save(role);
        User user = User.UserBuilder.anUser().withPassword("a".toCharArray()).withUsername(USERNAME).withRoles(List.of(role)).withDeposit(500L).build();
        role.setUsers(List.of(user));
        user = userRepository.save(user);
        createProduct(user);
        SecurityContextHolder.getContext().setAuthentication(jwtProvider.getAuthenticationByUsername(USERNAME));
        return user;
    }

    private void createProduct(User user) {
        Product product = Product.ProductBuilder.aProduct().withProductName(PRODUCT_NAME).withCost(100L).withAmountAvailable(100L).withUser(user).build();
        productRepository.save(product);
    }

    @Test
    public void getUser() {
        //given
        insertUser();
        UserResponse userResponse = userService.get();
        Assertions.assertEquals(userResponse.getUsername(), USERNAME);
    }


    @Test
    public void deleteUser() {
        //given
        insertUser();
        //when
        userService.delete();
        //then
        Assertions.assertTrue(userRepository.findByUsername(USERNAME).isEmpty());
        Assertions.assertTrue(((List<Product>) productRepository.findAll()).isEmpty());
        Assertions.assertFalse(((List<Role>) roleRepository.findAll()).isEmpty());
    }

    @Transactional
    @Test
    public void resetUserDeposit() {
        //given
        User user = insertUser();
        //when
        userService.reset();
        //then
        Assertions.assertEquals((long) userRepository.findById(user.getId()).get().getDeposit(), 0L);
    }

    @Test
    public void userDeposit() {
        //given
        User user = insertUser();
        DepositRequest depositRequest = DepositRequest.DepositRequestBuilder.aDepositRequest().withAmount(100L).withCoin(2l).build();
        //when
        userService.deposit(depositRequest);
        //then
        Assertions.assertEquals(userRepository.findById(user.getId()).get().getDeposit(), 700L);
    }

    @Transactional
    @Test
    public void registerUser() {
        //given
        insertUser();
        RegisterRequest registerRequest = RegisterRequest.RegisterRequestBuilder.aRegisterRequest().withPassword("f").withUsername("f").withRoles(List.of(ROLE_NAME, "RANDOM_ROLE")).build();
        //when
        Long id = userService.registerUser(registerRequest);
        Optional<User> user = userRepository.findById(id);

        //then
        Assertions.assertTrue(user.isPresent());
        Assertions.assertEquals(user.get().getUsername(), "f");
        Assertions.assertEquals(user.get().getRoles().get(0).getName(), ROLE_NAME);
        Assertions.assertEquals(user.get().getRoles().size(), 1);
    }

}
