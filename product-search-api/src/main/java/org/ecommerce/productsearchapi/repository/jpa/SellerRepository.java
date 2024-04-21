package org.ecommerce.productsearchapi.repository.jpa;

import org.ecommerce.product.entity.SellerRep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SellerRepository extends JpaRepository<SellerRep, Integer> {
}
