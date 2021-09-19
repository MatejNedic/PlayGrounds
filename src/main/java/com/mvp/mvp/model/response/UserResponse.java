package com.mvp.mvp.model.response;

import com.mvp.mvp.model.entity.User;

public class UserResponse {
    private String username;
    private Long deposit;

    public static UserResponse generateUserResponse(User user) {
        UserResponse userResponse = new UserResponse();
        userResponse.setDeposit(user.getDeposit());
        userResponse.setUsername(user.getUsername());
        return userResponse;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getDeposit() {
        return deposit;
    }

    public void setDeposit(Long deposit) {
        this.deposit = deposit;
    }
}
