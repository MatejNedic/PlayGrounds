package com.mvp.mvp.service;

import com.mvp.mvp.model.request.ChangePasswordRequest;
import com.mvp.mvp.model.request.DepositRequest;
import com.mvp.mvp.model.request.RegisterRequest;
import com.mvp.mvp.model.response.UserResponse;

public interface UserService {

    Long registerUser(RegisterRequest registerRequest);

    void deposit(DepositRequest depositRequest);

    void reset();

    void delete();

    void changePassword(ChangePasswordRequest changePasswordRequest);

    UserResponse get();
}
