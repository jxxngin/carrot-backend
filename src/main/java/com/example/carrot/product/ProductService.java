package com.example.carrot.product;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {

    private final List<Product> products = new CopyOnWriteArrayList<>(
            List.of(
                    new Product(1L, "원목 책상", 25000, "역삼동"),
                    new Product(2L, "자전거", 80000, "서초동")
            )
    );

    private final AtomicLong nextId = new AtomicLong(3);

    public List<Product> getProducts() {
        return List.copyOf(products);
    }

    public Product getProduct(Long id) {
        for (Product product : products) {
            if (product.id().equals(id)) {
                return product;
            }
        }

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "해당 상품을 찾을 수 없습니다."
        );
    }

    public Product createProduct(CreateProductRequest request) {
        if (request.title() == null || request.title().isBlank()
            || request.location() == null || request.location().isBlank()
            || request.price() < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "제목과 지역은 필수이며, 가격은 0 이상이어야 합니다."
            );
        }

        Product product = new Product(
                nextId.getAndIncrement(),
                request.title(),
                request.price(),
                request.location()
        );

        products.add(product);
        return product;
    }
}
