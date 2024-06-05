package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ProductResponse(
	Integer id,
	Integer price,
	String bizName,
	Integer stock,
	Integer favoriteCount,
	String category,
	String name,
	String status,
	String information,
	LocalDateTime createDatetime,
	Integer deliveryFee,
	List<ImageResponse> images,
	CategoryResponse categoryResponse
) {
}
