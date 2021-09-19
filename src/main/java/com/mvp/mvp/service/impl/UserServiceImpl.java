package com.mvp.mvp.service.impl;

import com.mvp.mvp.model.entity.Role;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.model.request.ChangePasswordRequest;
import com.mvp.mvp.model.request.DepositRequest;
import com.mvp.mvp.model.request.RegisterRequest;
import com.mvp.mvp.model.response.UserResponse;
import com.mvp.mvp.model.security.UserDetailsImpl;
import com.mvp.mvp.repository.RoleRepository;
import com.mvp.mvp.repository.UserRepository;
import com.mvp.mvp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final TransactionHandler transactionHandler;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TransactionHandler transactionHandler, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.transactionHandler = transactionHandler;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @Override
    public Long registerUser(RegisterRequest registerRequest) {
        List<Role> roles = roleRepository.findAllByName(registerRequest.getRoles());
        User user = User.UserBuilder.anUser().withPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()).toCharArray()).withRoles(roles)
                .withUsername(registerRequest.getUsername()).build();
        return userRepository.save(user).getId();
    }

    @Override
    public void deposit(DepositRequest depositRequest) {
        userRepository.addDepositToUserById(calculateAmountToDeposit(depositRequest), getIdFromSecurityContextHolder());
    }

    @Override
    public void reset() {
        userRepository.resetDeposit(getIdFromSecurityContextHolder());
    }

    @Override
    public void delete() {
        userRepository.deleteById(getIdFromSecurityContextHolder());
    }

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        userRepository.changePasswordById(getIdFromSecurityContextHolder(), String.valueOf(changePasswordRequest.getPassword()));
    }

    @Override
    public UserResponse get() {
        User user = transactionHandler.runInTransaction(this::fetchUser);
        return UserResponse.generateUserResponse(user);
    }

    private Long calculateAmountToDeposit(DepositRequest depositRequest) {
        return depositRequest.getCoin() * depositRequest.getAmount();
    }

    private User fetchUser() {
        Optional<User> optionalUser = userRepository.findById(getIdFromSecurityContextHolder());
        return optionalUser.orElseThrow(() -> new UsernameNotFoundException("SecurityContext holds invalid user!"));
    }

    private Long getIdFromSecurityContextHolder() {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId();
    }

}