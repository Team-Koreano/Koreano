package org.ecommerce.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class TestErrorCode implements ErrorCode{
	private int code;
	private String message;
}
