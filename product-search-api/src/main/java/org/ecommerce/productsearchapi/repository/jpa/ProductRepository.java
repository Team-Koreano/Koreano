package org.ecommerce.productsearchapi.repository.jpa;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.repository.ProductCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, ProductCustomRepository {
}
