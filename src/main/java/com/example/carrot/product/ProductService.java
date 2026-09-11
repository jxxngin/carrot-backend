package com.example.carrot.product;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class ProductService {

	private final ProductRepository productRepository;

	public ProductService(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	public List<Product> getProducts() {
		return productRepository.findAll(Sort.by("id").ascending())
			.stream()
			.map(this::toProduct)
			.toList();
	}

	public Product getProduct(Long id) {
		ProductEntity entity = productRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"해당 상품을 찾을 수 없습니다."
			));

		return toProduct(entity);
	}

	@Transactional
	public Product createProduct(CreateProductRequest request) {
		ProductEntity entity = new ProductEntity(
			request.title(),
			request.price(),
			request.location()
		);

		ProductEntity savedEntity = productRepository.save(entity);

		return toProduct(savedEntity);
	}

	@Transactional
	public Product updateProduct(Long id, UpdateProductRequest request) {
		ProductEntity entity = productRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"해당 상품을 찾을 수 없습니다."
			));

		entity.update(
			request.title(),
			request.price(),
			request.location()
		);

		return toProduct(entity);
	}

	@Transactional
	public void deleteProduct(Long id) {
		ProductEntity entity = productRepository.findById(id)
			.orElseThrow(() -> new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"해당 상품을 찾을 수 없습니다."
			));

		productRepository.delete(entity);
	}

	private Product toProduct(ProductEntity entity) {
		return new Product(
			entity.getId(),
			entity.getTitle(),
			entity.getPrice(),
			entity.getLocation()
		);
	}
}
