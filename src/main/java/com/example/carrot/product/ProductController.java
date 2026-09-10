package com.example.carrot.product;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/api/products")
    public List<Product> getProducts() {
        return productService.getProducts();
    }

    @GetMapping("/api/products/{id}")
    public Product getProduct(@PathVariable("id") Long id) {
        return productService.getProduct(id);
    }

    @PostMapping("/api/products")
    public ResponseEntity<Product> createProduct(
            @RequestBody CreateProductRequest request
    ) {
        Product product = productService.createProduct(request);

        return ResponseEntity
                .created(URI.create("/api/products/" + product.id()))
                .body(product);
    }

    @PutMapping("/api/products/{id}")
    public Product updateProduct(
            @PathVariable("id") Long id,
            @RequestBody UpdateProductRequest request
    ) {
        return productService.updateProduct(id, request);
    }
}
