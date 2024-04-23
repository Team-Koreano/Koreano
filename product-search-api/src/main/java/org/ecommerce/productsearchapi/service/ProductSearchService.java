package org.ecommerce.productsearchapi.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.dto.ProductMapper;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
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
	 * @apiNote 노원호
	 * product ID로 단일 레코드를 조회한다.
	 * @param  productId 상품 ID
	 * @return ProductSearchDto
	 */
	@Transactional(readOnly = true)
	public ProductSearchDto getProductById(final Integer productId) {

		final Product product = productRepository.findProductById(productId)
			.orElseThrow(() -> new CustomException(ProductSearchErrorCode.NOT_FOUND_PRODUCT_ID));

		return ProductMapper.INSTANCE.toDto(product);
	}

}
