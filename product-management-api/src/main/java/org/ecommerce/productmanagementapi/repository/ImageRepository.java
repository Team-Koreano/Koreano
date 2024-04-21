package org.ecommerce.productmanagementapi.repository;

import org.ecommerce.product.entity.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Integer> {
}
