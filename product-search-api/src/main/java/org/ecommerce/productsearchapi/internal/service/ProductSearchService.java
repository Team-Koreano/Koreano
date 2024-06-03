package org.ecommerce.productsearchapi.internal.service;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.document.ProductDocument;
import org.ecommerce.productsearchapi.dto.ProductMapper;
import org.ecommerce.productsearchapi.dto.ProductDto;
import org.ecommerce.productsearchapi.repository.ProductElasticsearchRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductSearchService {

	private final ProductElasticsearchRepository productElasticsearchRepository;

	/**
	 * @author ${no.oneho}
	 * es에 product 정보 저장
	 * @param  product 상품 정보
	 * @return ProductSearchDto
	 */
	public ProductDto saveProduct(Product product) {
		ProductDocument productDocument = ProductDocument.of(product);
		productElasticsearchRepository.save(productDocument);
		return ProductMapper.INSTANCE.documentToDto(productDocument);
	}

}
