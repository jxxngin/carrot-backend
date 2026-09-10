package com.example.carrot.product;

public record Product(
	Long id,
	String title,
	int price,
	String location
) {
}
