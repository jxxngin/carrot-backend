package com.example.carrot.product;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
public class ProductController {

	private final ProductService productService;

	public ProductController(ProductService productService) {
		this.productService = productService;
	}

	@GetMapping("/api/products")
	public ProductPageResponse getProducts(
		@RequestParam(name = "keyword", required = false) String keyword,
		@RequestParam(name = "page", defaultValue = "0") int page,
		@RequestParam(name = "size", defaultValue = "6") int size
	) {
		return productService.getProductPage(keyword, page, size);
	}

	@GetMapping("/api/products/{id}")
	public Product getProduct(@PathVariable("id") Long id) {
		return productService.getProduct(id);
	}

	@PostMapping("/api/products")
	public ResponseEntity<Product> createProduct(
		@Valid @RequestBody CreateProductRequest request
	) {
		Product product = productService.createProduct(request);

		return ResponseEntity
			.created(URI.create("/api/products/" + product.id()))
			.body(product);
	}

	@PutMapping("/api/products/{id}")
	public Product updateProduct(
		@PathVariable("id") Long id,
		@Valid @RequestBody UpdateProductRequest request
	) {
		return productService.updateProduct(id, request);
	}

	@DeleteMapping("/api/products/{id}")
	public ResponseEntity<Void> deleteProduct(
		@PathVariable("id") Long id
	) {
		productService.deleteProduct(id);

		return ResponseEntity.noContent().build();
	}
}
