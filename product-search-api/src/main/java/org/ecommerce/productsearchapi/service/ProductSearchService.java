package org.ecommerce.productsearchapi.service;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.productsearchapi.dto.ProductMapper;
import org.ecommerce.productsearchapi.dto.ProductSearchDto;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorCode;
import org.ecommerce.productsearchapi.repository.jpa.ProductRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

	private final ProductRepository productRepository;

	/**
	 * @apiNote 노원호
	 * name(상품명)을 통해 elasticsearch에 검색쿼리
	 * @param  productId 상품 ID
	 * @return 테스트 과정이므로 별 다른 변환 과정 없이 Map 객체 반환
	 */
	public ProductSearchDto getProductById(Integer productId) {

		return ProductMapper.INSTANCE.toDto(productRepository.findById(productId).orElseThrow(
				() -> new CustomException(ProductSearchErrorCode.NOT_FOUND_PRODUCT_ID)
		));

	}


}
