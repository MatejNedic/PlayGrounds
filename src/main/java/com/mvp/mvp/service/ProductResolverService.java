package com.mvp.mvp.service;

import com.mvp.mvp.model.entity.ProductResolverEnum;

public interface ProductResolverService {

    <T, X> X resolveCall(T request, Long id, ProductResolverEnum productResolverEnum);
}
