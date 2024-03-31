package org.ecommerce.common.advice;

import lombok.extern.slf4j.Slf4j;
import org.ecommerce.common.vo.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleException(Exception e) {
    log.error("\u001B[31mcode: "+HttpStatus.INTERNAL_SERVER_ERROR.value()+"\u001B[0m");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body("INTERNAL_SERVER_ERROR!");
//    return new Response<>(500, e.getMessage());
  }
}
