package org.ecommerce.productmanagementapi.repository;

import java.util.List;

import org.ecommerce.product.entity.Product;

public interface ProductCustomRepository {
	List<Product> findProductsById(List<Integer> productIds);
}
