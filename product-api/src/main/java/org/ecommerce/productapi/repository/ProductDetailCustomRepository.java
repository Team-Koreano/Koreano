package org.ecommerce.productapi.repository;

import org.ecommerce.productapi.entity.ProductDetail;

public interface ProductDetailCustomRepository {
	ProductDetail findByProductDetailId(Integer productDetailId);
}
