package com.mvp.mvp.model.request;

import com.mvp.mvp.model.entity.Product;
import com.mvp.mvp.model.entity.User;
import com.mvp.mvp.validator.IsDividableBy5;
import org.springframework.validation.annotation.Validated;

import javax.validation.ValidationException;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public class ProductRequest {
    private Long productId;
    @NotNull
    private String productName;
    @NotNull
    @Min(5)
    @IsDividableBy5
    private Long cost;
    @NotNull
    @Min(1)
    private Long amountAvailable;

    public ProductRequest(Long productId) {
        this.productId = productId;
    }

    public ProductRequest() {
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

    public Product createProductFromRequest(User user) {
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
        return "ProductRequest{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", cost=" + cost +
                ", amountAvailable=" + amountAvailable +
                '}';
    }

    public static final class ProductRequestBuilder {
        private Long productId;
        private String productName;
        private Long cost;
        private Long amountAvailable;

        private ProductRequestBuilder() {
        }

        public static ProductRequestBuilder aProductRequest() {
            return new ProductRequestBuilder();
        }

        public ProductRequestBuilder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public ProductRequestBuilder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public ProductRequestBuilder withCost(Long cost) {
            this.cost = cost;
            return this;
        }

        public ProductRequestBuilder withAmountAvailable(Long amountAvailable) {
            this.amountAvailable = amountAvailable;
            return this;
        }

        public ProductRequest build() {
            ProductRequest productRequest = new ProductRequest();
            productRequest.setProductId(productId);
            productRequest.setProductName(productName);
            productRequest.setCost(cost);
            productRequest.setAmountAvailable(amountAvailable);
            return productRequest;
        }
    }
}
