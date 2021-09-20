package com.mvp.mvp.util;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.request.UpdateRequest;
import org.springframework.stereotype.Component;

import javax.validation.ValidationException;

@Component
public class ProductUtil {
    public Product updateProduct(UpdateRequest productRequest, Product product) {
        if(productRequest.getProductName() != null) {
            product.setProductName(productRequest.getProductName());
        }
        if(productRequest.getCost() != null) {
            if(productRequest.getCost()%5 != 0 || product.getCost() < 0) {
                throw new ValidationException("Product price must be dividable by 5 or greater then 0");
            }
            product.setCost(productRequest.getCost());
        }
        if(productRequest.getAmountAvailable() != null) {
            if(productRequest.getAmountAvailable() < 0) {
                throw new ValidationException("Product price must be greater then 0");
            }
            product.setAmountAvailable(productRequest.getAmountAvailable());
        }
        return product;
    }
}
