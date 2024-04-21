package org.ecommerce.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FeignErrorCode implements ErrorCode{
	private int code;
	private String message;
}
