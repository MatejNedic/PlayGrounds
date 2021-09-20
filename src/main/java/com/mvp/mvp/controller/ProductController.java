package com.mvp.mvp.controller;

import com.mvp.mvp.model.entity.ProductResolverEnum;
import com.mvp.mvp.model.request.BuyRequest;
import com.mvp.mvp.model.request.ProductRequest;
import com.mvp.mvp.model.request.UpdateRequest;
import com.mvp.mvp.model.response.BuyProductResponse;
import com.mvp.mvp.model.response.ProductResponse;
import com.mvp.mvp.service.ProductResolverService;
import com.mvp.mvp.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final ProductResolverService productResolverService;
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Value("url")
    private String url;

    @Autowired
    public ProductController(ProductService productService, ProductResolverService productResolverService) {
        this.productService = productService;
        this.productResolverService = productResolverService;
    }

    @PreAuthorize(value = "@RuleHandler.checkRule('ROLE_SELLER')")
    @PostMapping("/save")
    public ResponseEntity<HttpStatus> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        logger.info("/save" + productRequest.toString());
        Long id = productService.save(productRequest);
        return ResponseEntity.status(HttpStatus.CREATED).header("foundOn", url+"/product/get/" +id).build();
    }

    @PreAuthorize(value = "@RuleHandler.checkRule('ROLE_SELLER')")
    @PutMapping("/update")
    public ResponseEntity<ProductResponse> updateProduct(@Valid @RequestBody UpdateRequest request) {
        logger.info("/update" + request.toString());
        ProductResponse productResponse = productResolverService.resolveCall(request, request.getProductId(), ProductResolverEnum.UPDATE);
        return ResponseEntity.ok(productResponse);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable("id") Long id) {
        logger.info("/get/{id} " +id);
        return ResponseEntity.ok(productService.getById(id));
    }

    @PreAuthorize(value = "@RuleHandler.checkRule('ROLE_BUYER')")
    @PostMapping("/buy")
    public ResponseEntity<BuyProductResponse> buy(@Valid @RequestBody BuyRequest buyRequest) {
        logger.info("/buy" + buyRequest.toString());
        return ResponseEntity.ok(productService.buyProducts(buyRequest));
    }

    @PreAuthorize(value = "@RuleHandler.checkRule('ROLE_SELLER')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
        logger.info("/delete/{id}" + id);
        productResolverService.resolveCall(id, id, ProductResolverEnum.DELETE);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}