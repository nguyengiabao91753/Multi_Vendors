package com.example.shopee.controller;

import com.example.shopee.entity.ProductEntity;
import com.example.shopee.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.*;


import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductApiController {

    @Autowired
    private ProductRepository productRepository;
    @GetMapping("")
    public List<ProductEntity> all() {
        return productRepository.findAllByStatus(1);
    }

    @GetMapping("/bestSellingProducts")
    public List<ProductEntity> getBestSelling() {
        return productRepository.findBestSellingProducts();
    }

    @GetMapping("/mostLikedProducts")
    public List<ProductEntity> getMostLiked() {
        return productRepository.findMostLikedProducts();
    }
}
