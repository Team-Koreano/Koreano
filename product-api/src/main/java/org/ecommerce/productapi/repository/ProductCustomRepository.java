package org.ecommerce.productapi.repository;


import java.util.List;

import org.ecommerce.productapi.entity.Product;

public interface ProductCustomRepository {

	List<Product> findProductsByIds(List<Integer> productIds);


	Product findProductById(final Integer id);

}
