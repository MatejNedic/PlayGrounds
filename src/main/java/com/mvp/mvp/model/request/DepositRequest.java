package com.mvp.mvp.model.request;

import com.mvp.mvp.validator.IsDividableBy5;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Validated
public class DepositRequest {
    @NotNull
    private Long amount;
    @NotNull
    @IsDividableBy5
    private Long coin;

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Long getCoin() {
        return coin;
    }

    public void setCoin(Long coin) {
        this.coin = coin;
    }

    @Override
    public String toString() {
        return "DepositRequest{" +
                "amount=" + amount +
                ", coin=" + coin +
                '}';
    }


    public static final class DepositRequestBuilder {
        private Long amount;
        private Long coin;

        private DepositRequestBuilder() {
        }

        public static DepositRequestBuilder aDepositRequest() {
            return new DepositRequestBuilder();
        }

        public DepositRequestBuilder withAmount(Long amount) {
            this.amount = amount;
            return this;
        }

        public DepositRequestBuilder withCoin(Long coin) {
            this.coin = coin;
            return this;
        }

        public DepositRequest build() {
            DepositRequest depositRequest = new DepositRequest();
            depositRequest.setAmount(amount);
            depositRequest.setCoin(coin);
            return depositRequest;
        }
    }
}
