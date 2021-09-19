package com.mvp.mvp.model.response;

import java.util.Map;

public class BuyProductResponse {
    private Long amountSpent;
    private String productName;
    private Map<Long, Long> coins;

    public static BuyProductResponse constructBuyProductResponse(Long amountSpent, String productName, Map<Long, Long> coins) {
        BuyProductResponse buyProductResponse = new BuyProductResponse();
        buyProductResponse.setProductName(productName);
        buyProductResponse.setAmountSpent(amountSpent);
        buyProductResponse.setCoins(coins);
        return buyProductResponse;
    }

    public Long getAmountSpent() {
        return amountSpent;
    }

    public void setAmountSpent(Long amountSpent) {
        this.amountSpent = amountSpent;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Map<Long, Long> getCoins() {
        return coins;
    }

    public void setCoins(Map<Long, Long> coins) {
        this.coins = coins;
    }
}
