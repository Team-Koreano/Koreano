package org.ecommerce.common.vo;

public record ErrorResponse<T>(Integer code, T result) {
}
