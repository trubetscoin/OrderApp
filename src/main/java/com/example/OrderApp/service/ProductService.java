package com.example.OrderApp.service;

import com.example.OrderApp.model.Product;
import com.example.OrderApp.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProductById(Long productID) {
        return productRepository.findById(productID).orElse(null);
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product updateProduct(Long productID, Product updatedProduct) {
        if (productRepository.existsById(productID)) {
            updatedProduct.setId(productID);
            return productRepository.save(updatedProduct);
        } else {
            return null;
        }
    }

    public Product updateProduct(Product updatedProduct) {
        if (productRepository.existsById(updatedProduct.getId())) {
            return productRepository.save(updatedProduct);
        } else {
            return null;
        }
    }

    public void deleteProductById(Long productID) {
        productRepository.deleteById(productID);
    }

    public void deleteProductsByIdInRange(Long startID, Long endID) {
        List<Product> orders = productRepository.findProductsByIdInRange(startID, endID);
        productRepository.deleteAll(orders);
    }
}
