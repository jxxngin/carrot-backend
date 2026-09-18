package com.example.carrot.product;

import java.util.List;

public record ProductPageResponse(
	List<Product> content,
	int page,
	int size,
	long totalElements,
	int totalPages,
	boolean hasNext
) {
}
