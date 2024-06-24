package org.ecommerce.productapi.repository.impl;

import static org.ecommerce.productapi.entity.QProductDetail.*;

import org.ecommerce.productapi.entity.ProductDetail;
import org.ecommerce.productapi.repository.ProductDetailCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductDetailRepositoryImpl implements ProductDetailCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public ProductDetail findByProductDetailId(Integer productDetailId) {
		return jpaQueryFactory.selectFrom(productDetail)
			.where(productDetail.id.eq(productDetailId))
			.fetchOne();
	}
}
