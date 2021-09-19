package com.mvp.mvp.service;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.request.BuyRequest;
import com.mvp.mvp.model.request.ProductRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.model.response.BuyProductResponse;
import com.mvp.mvp.model.response.ProductResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ProductService {

    BuyProductResponse buyProducts(BuyRequest buyRequest);

    Long save(ProductRequest productRequest);

    ProductResponse updateProduct(Product product, UpdateRequest productRequest);

    void delete(Product product);

    ProductResponse getById(Long id);
}
