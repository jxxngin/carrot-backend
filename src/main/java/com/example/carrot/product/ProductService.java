package com.example.carrot.product;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

	public List<Product> getProducts(String keyword) {
		String normalizedKeyword = keyword == null ? "" : keyword.strip();
		Sort sort = Sort.by("id").ascending();

		List<ProductEntity> entities;

		if (normalizedKeyword.isEmpty()) {
			entities = productRepository.findAll(sort);
		} else {
			entities = productRepository.findByTitleContainingIgnoreCase(normalizedKeyword, sort);
		}

		return entities.stream()
			.map(this::toProduct)
			.toList();
	}

	public ProductPageResponse getProductPage(String keyword, int page, int size) {
		if (page < 0) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"페이지 번호는 0 이상이어야 합니다."
			);
		}

		if (size < 1 || size > 50) {
			throw new ResponseStatusException(
				HttpStatus.BAD_REQUEST,
				"페이지 크기는 1 이상 50 이하여야 합니다."
			);
		}

		String normalizedKeyword = keyword == null ? "" : keyword.strip();

		Pageable pageable = PageRequest.of(
			page,
			size,
			Sort.by("id").ascending()
		);

		Page<ProductEntity> result;

		if (normalizedKeyword.isEmpty()) {
			result = productRepository.findAll(pageable);
		} else {
			result = productRepository.findByTitleContainingIgnoreCase(normalizedKeyword, pageable);
		}

		Page<Product> products = result.map(this::toProduct);

		return new ProductPageResponse(
			products.getContent(),
			products.getNumber(),
			products.getSize(),
			products.getTotalElements(),
			products.getTotalPages(),
			products.hasNext()
		);
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
