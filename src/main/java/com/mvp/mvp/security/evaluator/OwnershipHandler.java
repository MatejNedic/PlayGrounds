package com.mvp.mvp.security.evaluator;

import com.mvp.mvp.model.entity.Product;

import javax.validation.constraints.NotNull;

public interface OwnershipHandler {

    boolean checkIfUserOwns(@NotNull Product product);

}
