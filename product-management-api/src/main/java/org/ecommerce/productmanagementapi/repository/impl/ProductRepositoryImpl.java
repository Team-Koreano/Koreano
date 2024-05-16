package org.ecommerce.productmanagementapi.repository.impl;

import static org.ecommerce.product.entity.QProduct.*;

import java.util.List;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productmanagementapi.repository.ProductCustomRepository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Product> findProductById(List<Integer> productIds) {
		return jpaQueryFactory.selectFrom(product)
			.where(product.id.in(productIds))
			.fetch();
	}
}