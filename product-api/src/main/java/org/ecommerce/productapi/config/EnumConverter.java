package org.ecommerce.productapi.config;

import org.ecommerce.productapi.entity.enumerated.ProductStatus;
import org.springframework.core.convert.converter.Converter;


public class EnumConverter<T extends Enum<T>> implements Converter<String, ProductStatus> {


	@Override
	public ProductStatus convert(String source) {
		return ProductStatus.findByCode(source);
	}
}
