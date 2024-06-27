package org.ecommerce.productapi.repository;

import java.util.List;

import org.ecommerce.productapi.entity.Product;

public interface ProductCustomRepository {

	Product findProductById(final Integer id);

	Product findProductWithProductDetailsById(final Integer id);

	List<Product> findProductWithProductDetailsByIds(List<Integer> productIds);

}
