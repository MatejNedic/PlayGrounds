package com.mvp.mvp.model.entity;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;

    @Column(name = "product_name", unique = true)
    private String productName;

    @NotNull
    private Long cost;

    @NotNull
    private Long amountAvailable;

    @ManyToOne()
    @JoinColumn(name = "seller_id")
    private User user;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId.equals(product.productId) && productName.equals(product.productName) && cost.equals(product.cost) && amountAvailable.equals(product.amountAvailable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, productName, cost, amountAvailable);
    }


    public static final class ProductBuilder {
        private Long productId;
        private String productName;
        private Long cost;
        private Long amountAvailable;
        private User user;

        private ProductBuilder() {
        }

        public static ProductBuilder aProduct() {
            return new ProductBuilder();
        }

        public ProductBuilder withProductId(Long productId) {
            this.productId = productId;
            return this;
        }

        public ProductBuilder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public ProductBuilder withCost(Long cost) {
            this.cost = cost;
            return this;
        }

        public ProductBuilder withAmountAvailable(Long amountAvailable) {
            this.amountAvailable = amountAvailable;
            return this;
        }

        public ProductBuilder withUser(User user) {
            this.user = user;
            return this;
        }

        public Product build() {
            Product product = new Product();
            product.setProductId(productId);
            product.setProductName(productName);
            product.setCost(cost);
            product.setAmountAvailable(amountAvailable);
            product.setUser(user);
            return product;
        }
    }
}
