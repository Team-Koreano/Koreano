package org.ecommerce.productmanagementapi.repository.impl;

import static org.ecommerce.product.entity.QImage.*;
import static org.ecommerce.product.entity.QProduct.*;
import static org.ecommerce.product.entity.QSellerRep.*;

import java.util.List;
import java.util.Optional;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productmanagementapi.repository.ProductCustomRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Product> findProductsById(List<Integer> productIds) {
		return jpaQueryFactory.selectFrom(product)
			.leftJoin(product.images)
			.leftJoin(product.sellerRep, sellerRep)
			.fetchJoin()
			.where(product.id.in(productIds))
			.fetch();
	}

	@Override
	public Optional<Product> findProductById(Integer productId) {
		return Optional.ofNullable(jpaQueryFactory.selectFrom(product)
			.leftJoin(product.images, image).fetchJoin()
			.leftJoin(product.sellerRep, sellerRep)
			.fetchJoin()
			.where(product.id.eq(productId))
			.fetchFirst());
	}
}
