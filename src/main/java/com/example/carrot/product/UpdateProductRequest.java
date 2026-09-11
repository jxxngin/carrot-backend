package com.example.carrot.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductRequest(
	@NotBlank(message = "제목은 필수입니다.")
	String title,

	@NotNull(message = "가격은 필수입니다.")
	@PositiveOrZero(message = "가격은 0 이상이어야 합니다.")
	Integer price,

	@NotBlank(message = "지역은 필수입니다.")
	String location
) {
}
