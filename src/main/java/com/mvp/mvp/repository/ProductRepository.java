package com.mvp.mvp.repository;

import com.mvp.mvp.model.entity.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ProductRepository extends CrudRepository<Product, Long> {


    @Modifying(clearAutomatically = true)
    @Query("update Product pr set pr.amountAvailable = pr.amountAvailable - :amount where pr.productId = :id")
    void updateProductByAmount(@Param("amount") Long amount, @Param("id") Long id);

    @Query("select pr from Product pr where pr.productName = :productName")
    Optional<Product> finByName(@Param("productName") String productName);
}
