package org.ecommerce.productapi.repository.impl;

import static org.ecommerce.productapi.entity.QProduct.*;

import java.util.List;

import org.ecommerce.productapi.entity.Product;
import org.ecommerce.productapi.repository.ProductCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public List<Product> findProductsByIds(List<Integer> productIds) {
		return jpaQueryFactory.selectFrom(product)
			.where(product.id.in(productIds))
			.fetch();
	}

	@Override
	public Product findProductById(final Integer id) {

		return jpaQueryFactory.selectFrom(product)
			.leftJoin(product.images).fetchJoin()
			.where(product.id.eq(id))
			.fetchFirst()
			;
	}
}
