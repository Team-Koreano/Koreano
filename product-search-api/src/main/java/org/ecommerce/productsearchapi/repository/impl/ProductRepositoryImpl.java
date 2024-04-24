package org.ecommerce.productsearchapi.repository.impl;

import static org.ecommerce.product.entity.QProduct.*;
import static org.ecommerce.product.entity.QImage.*;

import java.util.Optional;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.repository.ProductCustomRepository;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<Product> findProductById(final Integer id) {

		return Optional.ofNullable(jpaQueryFactory.selectFrom(product)
			.leftJoin(product.images, image)
			.fetchJoin()
			.where(product.id.eq(id))
			.orderBy(image.sequenceNumber.asc())
			.fetchFirst())
			;
	}
}
