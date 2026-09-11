package com.example.carrot.common.error;

public record FieldViolation(
	String field,
	String message
) {
}
