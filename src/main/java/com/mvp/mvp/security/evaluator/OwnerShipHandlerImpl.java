package com.mvp.mvp.security.evaluator;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.security.UserDetailsImpl;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component("OwnerHandler")
public class OwnerShipHandlerImpl implements OwnershipHandler {
    @Override
    public boolean checkIfUserOwns(@NotNull Product product) {
        return ((UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser().getId().equals(product.getUser().getId());
    }
}
