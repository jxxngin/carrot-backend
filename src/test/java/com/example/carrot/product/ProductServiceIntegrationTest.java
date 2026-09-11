package com.example.carrot.product;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

// 테스트 트랜잭션 안에서 CRUD와 DB 반영을 검증한다.
// Service가 독립적으로 트랜잭션을 시작하고 커밋하는지는 검증하지 않는다.
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class ProductServiceIntegrationTest {

	@Autowired
	private ProductService productService;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private EntityManager entityManager;

	@Test
	@DisplayName("상품을 등록하고 수정한 뒤 삭제할 수 있다")
	void createsUpdatesAndDeletesProduct() {
		// 등록
		Product created = productService.createProduct(
			new CreateProductRequest("keyboard", 15000, "Seoul")
		);

		assertThat(created.id()).isNotNull();

		entityManager.flush();
		entityManager.clear();

		// DB에서 다시 조회
		Product found = productService.getProduct(created.id());

		assertThat(found.title()).isEqualTo("keyboard");
		assertThat(found.price()).isEqualTo(15000);
		assertThat(found.location()).isEqualTo("Seoul");

		// 수정
		productService.updateProduct(
			created.id(),
			new UpdateProductRequest("updated keyboard", 12000, "Busan")
		);

		entityManager.flush();
		entityManager.clear();

		// 변경 감지로 DB에 반영됐는지 확인
		Product updated = productService.getProduct(created.id());

		assertThat(updated.title()).isEqualTo("updated keyboard");
		assertThat(updated.price()).isEqualTo(12000);
		assertThat(updated.location()).isEqualTo("Busan");

		// 삭제
		productService.deleteProduct(created.id());

		entityManager.flush();
		entityManager.clear();

		assertThat(productRepository.findById(created.id())).isEmpty();
	}
}
