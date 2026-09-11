package com.example.carrot.common.error;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiErrorResponse> handleValidation(
		MethodArgumentNotValidException exception
	) {
		List<FieldViolation> errors = exception.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(error -> new FieldViolation(
				error.getField(),
				error.getDefaultMessage()
			))
			.sorted(Comparator.comparing(FieldViolation::field)
				.thenComparing(FieldViolation::message))
			.toList();

		ApiErrorResponse response = new ApiErrorResponse(
			400,
			"입력값을 확인해주세요.",
			errors
		);

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(ResponseStatusException.class)
	public ResponseEntity<ApiErrorResponse> handleResponseStatus(
		ResponseStatusException exception
	) {
		String message = exception.getReason();

		if (message == null) {
			message = "요청을 처리할 수 없습니다.";
		}

		ApiErrorResponse response = new ApiErrorResponse(
			exception.getStatusCode().value(),
			message,
			List.of()
		);

		return ResponseEntity.status(exception.getStatusCode())
			.headers(exception.getHeaders())
			.body(response);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiErrorResponse> handleUnreadableBody(
		HttpMessageNotReadableException exception
	) {
		ApiErrorResponse response = new ApiErrorResponse(
			400,
			"요청 본문의 JSON 형식과 값의 타입을 확인해주세요.",
			List.of()
		);

		return ResponseEntity.badRequest().body(response);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiErrorResponse> handleTypeMismatch(
		MethodArgumentTypeMismatchException exception
	) {
		ApiErrorResponse response = new ApiErrorResponse(
			400,
			"요청 파라미터의 타입을 확인해주세요.",
			List.of(
				new FieldViolation(
					exception.getName(),
					"요청한 값의 타입이 올바르지 않습니다."
				)
			)
		);

		return ResponseEntity.badRequest().body(response);
	}
}
