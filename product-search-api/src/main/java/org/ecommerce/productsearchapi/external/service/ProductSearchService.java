package org.ecommerce.productsearchapi.external.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.dto.ProductDtoWithImageListDto;
import org.ecommerce.productsearchapi.dto.ProductMapper;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorCode;
import org.ecommerce.productsearchapi.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

	private final ProductRepository productRepository;

	/**
	 * @param productId 상품 ID
	 * @return ProductSearchDto
	 * @author ${no.oneho}
	 * product ID로 단일 레코드를 조회한다.
	 */
	@Transactional(readOnly = true)
	public ProductDtoWithImageListDto getProductById(final Integer productId) {

		final Product product = productRepository.findProductById(productId);

		if (product == null) {
			throw new CustomException(ProductSearchErrorCode.NOT_FOUND_PRODUCT_ID);
		}

		return ProductMapper.INSTANCE.entityToDtoWithImageList(product);
	}

}
