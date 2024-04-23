package org.ecommerce.orderapi.client;

import java.util.List;

import org.ecommerce.orderapi.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-search-service", url = "${product-search-service.url}")
public interface ProductSearchServiceClient {

	/**
	 * 재고를 확인하는 Internal API
	 * <p>
	 * 1. 상품의 유효성 검사를 합니다.
	 * 2. 주문 상품의 수량과 현재 남아있는 재고량을 비교 검증을 합니다.
	 * <p>
	 * @author ${Juwon}
	 *
	 * @param productIds - 상품 번호가 들어간 리스트
	 * @param quantities - 상품 수량이 들어간 리스트
	 * @return ProductDto.Response - 재고를 확인한 상품의 정보
	*/
	@GetMapping
	List<ProductDto.Response> checkStocks(
			@RequestParam("productIds") final List<Integer> productIds,
			@RequestParam("quantities") final List<Integer> quantities
	);
}