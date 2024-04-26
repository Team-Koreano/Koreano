package org.ecommerce.productmanagementapi.repository;

import java.util.Optional;

import org.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product,Integer> {
	Optional<Product> findProductById(Integer productId);
}
