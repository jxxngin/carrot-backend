package com.example.carrot.product;

public record CreateProductRequest(
        String title,
        int price,
        String location
) {
}
