package org.ecommerce.productmanagementapi.repository;

import org.ecommerce.product.entity.SellerRep;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<SellerRep,Integer> {
}
