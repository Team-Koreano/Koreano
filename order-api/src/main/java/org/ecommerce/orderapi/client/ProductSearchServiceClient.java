package org.ecommerce.orderapi.client;

import java.util.List;

import org.ecommerce.orderapi.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-search-service", url = "${product-search-service.url}")
public interface ProductSearchServiceClient {

	// 장바구니에 담겨있는 상품 정보를 가져옴, 검증
	@GetMapping
	List<ProductDto.Response> checkStocks(
			@RequestParam("productIds") final List<Integer> productIds,
			@RequestParam("quantities") final List<Integer> quantities
	);
}