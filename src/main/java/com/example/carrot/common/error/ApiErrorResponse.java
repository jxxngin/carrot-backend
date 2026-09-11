package com.example.carrot.common.error;

import java.util.List;

public record ApiErrorResponse(
	int status,
	String message,
	List<FieldViolation> errors
) {
}
