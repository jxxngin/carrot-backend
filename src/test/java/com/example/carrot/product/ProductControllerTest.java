package com.example.carrot.product;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

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
}
