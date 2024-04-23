package org.ecommerce.productsearchapi.repository.impl;

import java.util.Optional;

import org.ecommerce.productsearchapi.dto.ProductSearchDto;

public interface ProductCustomRepository {

	Optional<ProductSearchDto> findProductById(final Integer id);

}
