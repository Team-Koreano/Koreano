package org.ecommerce.productmanagementapi.repository;

import java.util.List;
import java.util.Optional;

import org.ecommerce.product.entity.Product;

public interface ProductCustomRepository {
	List<Product> findProductsById(List<Integer> productIds);

	Optional<Product> findProductById(Integer productId);
}
