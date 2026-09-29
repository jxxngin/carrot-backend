package com.example.carrot.product;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	Page<ProductEntity> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
}
