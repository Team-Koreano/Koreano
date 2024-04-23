package org.ecommerce.productsearchapi.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorCode;
import org.ecommerce.productsearchapi.repository.ProductRepository;
import org.ecommerce.productsearchapi.repository.impl.ProductRepositoryImpl;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

	private final ProductRepository productRepository;

	/**
	 * @apiNote 노원호
	 * product ID로 단일 레코드를 조회한다.
	 * @param  productId 상품 ID
	 * @return ProductSearchDto
	 */
	public ProductSearchDto getProductById(final Integer productId) {
		return productRepository.findProductById(productId)
			.orElseThrow(() -> new CustomException(ProductSearchErrorCode.NOT_FOUND_PRODUCT_ID));
	}

}
