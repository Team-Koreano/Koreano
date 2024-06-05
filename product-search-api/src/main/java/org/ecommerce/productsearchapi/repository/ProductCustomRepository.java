package org.ecommerce.productsearchapi.repository;


import org.ecommerce.product.entity.Product;

public interface ProductCustomRepository {

	Product findProductById(final Integer id);

}
