package org.ecommerce.common.vo;

import lombok.Builder;
import lombok.Getter;

public record Response<T>(Integer status, T result) {
}
