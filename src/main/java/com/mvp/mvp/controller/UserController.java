package com.mvp.mvp.controller;

import com.mvp.mvp.model.request.ChangePasswordRequest;
import com.mvp.mvp.model.request.DepositRequest;
import com.mvp.mvp.model.request.RegisterRequest;
import com.mvp.mvp.model.response.UserResponse;
import com.mvp.mvp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController()
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Value("url")
    private String url;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatus> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("/register " +registerRequest.toString());
        Long id = userService.registerUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).header("foundOn", url+ "/user/get/" +id).build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<HttpStatus> delete() {
        userService.delete();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PreAuthorize(value = "@RuleHandler.checkRule('ROLE_BUYER')")
    @PostMapping("/deposit")
    public ResponseEntity<HttpStatus> deposit(@Valid @RequestBody DepositRequest depositRequest) {
        logger.info("/deposit " +depositRequest.toString());
        userService.deposit(depositRequest);
        return ResponseEntity.ok().build();
    }

    //test is not written for this case since I was not sure if it was wanted as endpoint!
    @PutMapping("/changePassword")
    public ResponseEntity<HttpStatus> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        logger.info("/changePassword " +changePasswordRequest.toString());
        userService.changePassword(changePasswordRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get")
    public ResponseEntity<UserResponse> getUser() {
        return ResponseEntity.ok(userService.get());
    }

    @PreAuthorize(value = "@RuleHandler.checkRule('ROLE_BUYER')")
    @GetMapping("/reset")
    public ResponseEntity<HttpStatus> resetDeposit() {
        userService.reset();
        return ResponseEntity.ok().build();
    }

}