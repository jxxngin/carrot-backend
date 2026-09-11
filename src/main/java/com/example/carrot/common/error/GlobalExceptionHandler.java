package com.example.carrot.common.error;

import java.util.Comparator;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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
}
