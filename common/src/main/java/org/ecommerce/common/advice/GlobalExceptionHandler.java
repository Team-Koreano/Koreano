package org.ecommerce.common.advice;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.common.vo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Response<String>> handleCustomException(CustomException e) {
		Response<String> errorResponse = new Response<>(e.getErrorCode().getCode(), e.getErrorMessage());

		return ResponseEntity.status(e.getErrorCode().getCode()).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<String>> handleException(Exception e) {

		Response<String> errorResponse = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
			e.getMessage());

		log.error("\u001B[31mcode: " + HttpStatus.INTERNAL_SERVER_ERROR.value() + "\u001B[0m");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorResponse);
	}

}
