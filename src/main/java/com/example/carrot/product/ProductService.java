package com.example.carrot.product;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

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
        if (request.title() == null || request.title().isBlank()
            || request.location() == null || request.location().isBlank()
            || request.price() == null || request.price() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "제목과 지역은 필수이며, 가격은 0 이상이어야 합니다."
            );
        }

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
        if (request.title() == null || request.title().isBlank()
                || request.location() == null || request.location().isBlank()
                || request.price() == null || request.price() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "제목과 지역은 필수이며, 가격은 0 이상이어야 합니다."
            );
        }

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
