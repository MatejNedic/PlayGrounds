package com.mvp.mvp.model.response;

import com.mvp.mvp.model.entity.Product;

public class ProductResponse {

    private String productName;
    private Long cost;
    private Long amountAvailable;

    public static ProductResponse createProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setProductName(product.getProductName());
        productResponse.setAmountAvailable(product.getAmountAvailable());
        productResponse.setCost(product.getCost());
        return productResponse;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public Long getAmountAvailable() {
        return amountAvailable;
    }

    public void setAmountAvailable(Long amountAvailable) {
        this.amountAvailable = amountAvailable;
    }
}
