package com.mvp.mvp.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Validated
public class RegisterRequest {

    @NotNull
    private String username;
    @NotNull
    private String password;
    @NotNull
    @JsonProperty("roles")
    private List<String> roles = new ArrayList<>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                '}';
    }


    public static final class RegisterRequestBuilder {
        private String username;
        private String password;
        private List<String> roles = new ArrayList<>();

        private RegisterRequestBuilder() {
        }

        public static RegisterRequestBuilder aRegisterRequest() {
            return new RegisterRequestBuilder();
        }

        public RegisterRequestBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public RegisterRequestBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public RegisterRequestBuilder withRoles(List<String> roles) {
            this.roles = roles;
            return this;
        }

        public RegisterRequest build() {
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setPassword(password);
            registerRequest.setRoles(roles);
            return registerRequest;
        }
    }
}
