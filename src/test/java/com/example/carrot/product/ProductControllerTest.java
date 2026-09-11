package com.example.carrot.product;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import com.example.carrot.common.error.GlobalExceptionHandler;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
public class ProductControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProductService productService;

	@Test
	@DisplayName("제목이 공백이면 400과 제목 오류를 반환한다")
	void rejectsBlankTitle() throws Exception {
		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"title": "   ",
						"price": 1000,
						"location": "Seoul"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.errors.length()").value(1))
			.andExpect(jsonPath("$.errors[0].field").value("title"))
			.andExpect(jsonPath("$.errors[0].message").value("제목은 필수입니다."));

		verifyNoInteractions(productService);
	}

	@Test
	@DisplayName("가격이 누락되면 400과 가격 오류를 반환한다")
	void rejectsMissingPrice() throws Exception {
		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"title": "keyboard",
						"location": "Seoul"
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.errors.length()").value(1))
			.andExpect(jsonPath("$.errors[0].field").value("price"))
			.andExpect(jsonPath("$.errors[0].message").value("가격은 필수입니다."));

		verifyNoInteractions(productService);
	}

	@Test
	@DisplayName("JSON 문법이 잘못되면 400과 본문 오류를 반환한다")
	void rejectsMalformedJson() throws Exception {
		mockMvc.perform(post("/api/products")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"title\":"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.message")
				.value("요청 본문의 JSON 형식과 값의 타입을 확인해주세요."))
			.andExpect(jsonPath("$.errors").isEmpty());

		verifyNoInteractions(productService);
	}

	@Test
	@DisplayName("상품 ID가 숫자가 아니면 400과 ID 오류를 반환한다")
	void rejectsNonNumericId() throws Exception {
		mockMvc.perform(get("/api/products/abc"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.errors[0].field").value("id"))
			.andExpect(jsonPath("$.message")
				.value("요청 파라미터의 타입을 확인해주세요."));

		verifyNoInteractions(productService);
	}

	@Test
	@DisplayName("상품을 조회하면 200과 상품 정보를 반환한다")
	void returnsProduct() throws Exception {
		Product product = new Product(
			1L,
			"keyboard",
			15000,
			"Seoul"
		);

		given(productService.getProduct(1L))
			.willReturn(product);

		mockMvc.perform(get("/api/products/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.title").value("keyboard"))
			.andExpect(jsonPath("$.price").value(15000))
			.andExpect(jsonPath("$.location").value("Seoul"));

		verify(productService).getProduct(1L);
	}

	@Test
	@DisplayName("상품이 없으면 404와 공통 오류 응답을 반환한다")
	void returnsNotFoundForMissingProduct() throws Exception {
		given(productService.getProduct(999L))
			.willThrow(new ResponseStatusException(
				HttpStatus.NOT_FOUND,
				"해당 상품을 찾을 수 없습니다."
			));

		mockMvc.perform(get("/api/products/999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.status").value(404))
			.andExpect(jsonPath("$.message")
				.value("해당 상품을 찾을 수 없습니다."))
			.andExpect(jsonPath("$.errors").isEmpty());

		verify(productService).getProduct(999L);
	}
}
