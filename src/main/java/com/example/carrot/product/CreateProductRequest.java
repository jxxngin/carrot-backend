package com.example.carrot.product;

public record CreateProductRequest(
        String title,
        Integer price,
        String location
) {
}
