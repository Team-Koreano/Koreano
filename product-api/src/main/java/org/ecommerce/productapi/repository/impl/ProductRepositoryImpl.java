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
	public Product findProductById(final Integer id) {
		return jpaQueryFactory.selectFrom(product)
			.leftJoin(product.images).fetchJoin()
			.where(product.id.eq(id))
			.fetchFirst()
			;
	}

	@Override
	public Product findProductWithProductDetailsById(Integer id) {
		return jpaQueryFactory.selectFrom(product)
			.leftJoin(product.sellerRep).fetchJoin()
			.leftJoin(product.images).fetchJoin()
			.where(product.id.eq(id))
			.fetchFirst()
			;
	}

	@Override
	public List<Product> findProductWithProductDetailsByIds(List<Integer> productIds) {
		return jpaQueryFactory.selectFrom(product)
			.leftJoin(product.sellerRep).fetchJoin()
			.leftJoin(product.images).fetchJoin()
			.where(product.id.in(productIds))
			.fetch();
	}
}
