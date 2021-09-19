package com.mvp.mvp.model.request;

import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Validated
public class BuyRequest {

    @NotNull
    private String productName;
    @NotNull
    @Min(1)
    private Long amount;

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }


    public static final class BuyRequestBuilder {
        private String productName;
        private Long amount;

        private BuyRequestBuilder() {
        }

        public static BuyRequestBuilder aBuyRequest() {
            return new BuyRequestBuilder();
        }

        public BuyRequestBuilder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public BuyRequestBuilder withAmount(Long amount) {
            this.amount = amount;
            return this;
        }

        public BuyRequest build() {
            BuyRequest buyRequest = new BuyRequest();
            buyRequest.setProductName(productName);
            buyRequest.setAmount(amount);
            return buyRequest;
        }
    }

    @Override
    public String toString() {
        return "BuyRequest{" +
                "productName='" + productName + '\'' +
                ", amount=" + amount +
                '}';
    }
}
