package org.ecommerce.productsearchapi.repository;

import org.ecommerce.productsearchapi.entity.SellerRep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<SellerRep, Integer> {
}
