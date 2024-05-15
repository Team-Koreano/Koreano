package org.ecommerce.productmanagementapi.repository;

import org.ecommerce.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductCustomRepository {
}
