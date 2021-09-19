package com.mvp.mvp.model.request;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.User;

import javax.validation.constraints.NotNull;

public class UpdateRequest {
    @NotNull
    private Long productId;
    private String productName;
    private Long cost;
    private Long amountAvailable;

    public UpdateRequest(Long productId) {
        this.productId = productId;
    }

    public UpdateRequest() {
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

    public Product createUpdateRequest(User user) {
        Product product = new Product();
        product.setProductName(productName);
        product.setCost(cost);
        product.setAmountAvailable(amountAvailable);
        product.setUser(user);
        user.getProducts().add(product);
        return product;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }


    @Override
    public String toString() {
        return "UpdateRequest{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", cost=" + cost +
                ", amountAvailable=" + amountAvailable +
                '}';
    }

    public static final class updateRequestBuilder {
        private Long productId;
        private String productName;
        private Long cost;
        private Long amountAvailable;

        private updateRequestBuilder() {
        }

        public static UpdateRequest.updateRequestBuilder aupdateRequest() {
            return new UpdateRequest.updateRequestBuilder();
        }

        public UpdateRequest.updateRequestBuilder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public UpdateRequest.updateRequestBuilder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public UpdateRequest.updateRequestBuilder withCost(Long cost) {
            this.cost = cost;
            return this;
        }

        public UpdateRequest.updateRequestBuilder withAmountAvailable(Long amountAvailable) {
            this.amountAvailable = amountAvailable;
            return this;
        }

        public UpdateRequest build() {
            UpdateRequest updateRequest = new UpdateRequest();
            updateRequest.setProductId(productId);
            updateRequest.setProductName(productName);
            updateRequest.setCost(cost);
            updateRequest.setAmountAvailable(amountAvailable);
            return updateRequest;
        }
    }
}
