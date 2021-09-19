package com.mvp.mvp.model.request;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public class ChangePasswordRequest {
    @NotNull
    private char[] password;

    public char[] getPassword() {
        return password;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
