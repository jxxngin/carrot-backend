package com.example.carrot.product;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
	List<ProductEntity> findByTitleContainingIgnoreCase(String keyword, Sort sort);
}
