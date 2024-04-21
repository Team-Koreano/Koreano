package org.ecommerce.common.advice;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.common.vo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Response<String>> handleValidationException(MethodArgumentNotValidException e) {
		final String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
		Response<String> errorResponse = new Response<>(HttpStatus.BAD_REQUEST.value(),errorMessage);

		return ResponseEntity.badRequest().body(errorResponse);
	}

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Response<String>> handleCustomException(CustomException e) {
		Response<String> errorResponse = new Response<>(e.getErrorCode().getCode(), e.getErrorMessage());

		return ResponseEntity.status(HttpStatus.BAD_REQUEST.value()).body(errorResponse);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Response<String>> handleException(Exception e) {

		Response<String> errorResponse = new Response<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),
			e.getMessage());

		log.error("\u001B[31mcode: " + HttpStatus.INTERNAL_SERVER_ERROR.value() + "\u001B[0m");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(errorResponse);
	}

}
