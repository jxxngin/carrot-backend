package com.example.carrot.product;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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

	@Test
	@DisplayName("제목에 검색어가 포함된 상품만 대소문자를 무시하고 조회한다")
	void searchesProductsByTitleIgnoringCase() {
		Product first = productService.createProduct(
			new CreateProductRequest("Wireless keyboard", 15000, "Seoul")
		);
		productService.createProduct(
			new CreateProductRequest("Mouse", 5000, "Keyboard Town")
		);
		Product second = productService.createProduct(
			new CreateProductRequest("Mini keyboard", 10000, "Busan")
		);

		entityManager.flush();
		entityManager.clear();

		assertThat(productService.getProducts("KEYBOARD"))
			.extracting(Product::id)
			.containsExactly(first.id(), second.id());
	}

	@Test
	@DisplayName("한글 검색어의 앞뒤 공백을 제거하고 조회한다")
	void searchesProductsWithTrimmedKoreanKeyword() {
		Product keyboard = productService.createProduct(
			new CreateProductRequest("무선 키보드", 15000, "잠실동")
		);
		productService.createProduct(
			new CreateProductRequest("마우스", 5000, "역삼동")
		);

		entityManager.flush();
		entityManager.clear();

		assertThat(productService.getProducts("  키보드  "))
			.extracting(Product::id)
			.containsExactly(keyboard.id());
	}

	@Test
	@DisplayName("검색어에 해당하는 상품이 없으면 빈 목록을 반환한다")
	void returnsEmptyListWhenNoTitleMatches() {
		productService.createProduct(
			new CreateProductRequest("Keyboard", 15000, "Seoul")
		);

		entityManager.flush();
		entityManager.clear();

		assertThat(productService.getProducts("monitor")).isEmpty();
	}

	@ParameterizedTest
	@NullAndEmptySource
	@ValueSource(strings = {"   "})
	@DisplayName("검색어가 없거나 공백이면 전체 상품을 조회한다")
	void returnsAllProductsWithoutKeyword(String keyword) {
		Product first = productService.createProduct(
			new CreateProductRequest("Keyboard", 15000, "Seoul")
		);
		Product second = productService.createProduct(
			new CreateProductRequest("Mouse", 5000, "Busan")
		);

		entityManager.flush();
		entityManager.clear();

		assertThat(productService.getProducts(keyword))
			.extracting(Product::id)
			.containsExactly(first.id(), second.id());
	}
}
