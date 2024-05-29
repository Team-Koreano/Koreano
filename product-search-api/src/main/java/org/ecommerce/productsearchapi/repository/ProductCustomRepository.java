package org.ecommerce.productsearchapi.repository;

import java.util.Optional;

import org.ecommerce.product.entity.Product;

public interface ProductCustomRepository {

	Product findProductById(final Integer id);

}
