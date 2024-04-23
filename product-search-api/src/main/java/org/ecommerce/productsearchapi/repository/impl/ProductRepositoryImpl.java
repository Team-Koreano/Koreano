package org.ecommerce.productsearchapi.repository.impl;

import org.ecommerce.productsearchapi.dto.ImageDto;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

import static org.ecommerce.product.entity.QProduct.product;
import static org.ecommerce.product.entity.QImage.image;

import java.util.Optional;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductCustomRepository {

	private final JPAQueryFactory jpaQueryFactory;

	@Override
	public Optional<ProductSearchDto> findProductById(final Integer id) {

		return Optional.ofNullable(jpaQueryFactory.select(Projections.constructor(ProductSearchDto.class,
				product.id,
				product.category,
				product.price,
				product.stock,
				product.sellerRep,
				product.favoriteCount,
				product.isDecaf,
				product.name,
				product.bean,
				product.acidity,
				product.information,
				product.status,
				product.isCrush,
				product.createDatetime,
				product.updateDatetime,
				Projections.list(Projections.constructor(ImageDto.class,
					image.id,
					image.isThumbnail,
					image.sequenceNumber,
					image.createDatetime,
					image.updateDatetime,
					image.imageUrl
				))
			))
			.from(product)
			.leftJoin(image)
			.on(image.product.id.eq(product.id))
			.where(product.id.eq(id))
			.limit(1)
			.fetchOne());
	}
}
