package org.ecommerce.common.error;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

  private final ErrorCode errorCode;
  private final String errorMessage;

  public CustomException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
    this.errorMessage = errorCode.getMessage();
  }

}
