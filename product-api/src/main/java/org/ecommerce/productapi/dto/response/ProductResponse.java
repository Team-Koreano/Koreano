package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
	Integer id,
	String bizName,
	Integer favoriteCount,
	String category,
	String name,
	String information,
	LocalDateTime createDatetime,
	Integer deliveryFee,
	List<ImageResponse> images,
	List<ProductDetailResponse> productDetails,
	CategoryResponse categoryResponse
) {
}
