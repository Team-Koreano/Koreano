package org.ecommerce.productapi.repository;

import org.ecommerce.productapi.entity.ProductDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer>, ProductDetailCustomRepository {
}
