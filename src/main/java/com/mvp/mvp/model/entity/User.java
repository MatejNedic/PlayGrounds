package com.mvp.mvp.model.entity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "user_vending")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotNull
    @Column(unique = true)
    private String username;
    private char[] password;
    private Long deposit = 0L;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Product> products = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "role_id", referencedColumnName = "id"))
    private List<Role> roles;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }

    public Long getDeposit() {
        return deposit;
    }

    public void setDeposit(Long deposit) {
        this.deposit = deposit;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Arrays.equals(password, user.password) && Objects.equals(deposit, user.deposit);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, username, deposit);
        result = 31 * result + Arrays.hashCode(password);
        return result;
    }


    public static final class UserBuilder {
        private String username;
        private char[] password;
        private Long deposit = 0L;
        private List<Role> roles;

        private UserBuilder() {
        }

        public static UserBuilder anUser() {
            return new UserBuilder();
        }

        public UserBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public UserBuilder withDeposit(Long deposit) {
            this.deposit = deposit;
            return this;
        }

        public UserBuilder withPassword(char[] password) {
            this.password = password;
            return this;
        }

        public UserBuilder withRoles(List<Role> roles) {
            this.roles = roles;
            return this;
        }

        public User build() {
            User user = new User();
            user.setDeposit(deposit);
            user.setUsername(username);
            user.setPassword(password);
            user.setRoles(roles);
            return user;
        }
    }
}
