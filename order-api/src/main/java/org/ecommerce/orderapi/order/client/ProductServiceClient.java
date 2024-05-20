package org.ecommerce.orderapi.order.client;

import java.util.List;

import org.ecommerce.common.config.FeignConfig;
import org.ecommerce.orderapi.order.dto.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service", url = "${product-search-service.url}", configuration = FeignConfig.class)
public interface ProductServiceClient {

	// 장바구니에 담겨있는 상품 정보를 가져옴, 검증
	// jwt 도입 이후 userId 삭제
	@GetMapping
	List<ProductDto.Response> getProducts(
			@RequestParam("productIds") final List<Integer> productIds
	);
}
