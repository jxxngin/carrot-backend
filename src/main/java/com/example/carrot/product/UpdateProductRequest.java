package com.example.carrot.product;

public record UpdateProductRequest(
        String title,
        Integer price,
        String location
) {
}
