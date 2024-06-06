package org.ecommerce.productsearchapi.dto;

import java.time.LocalDateTime;

public record ImageDto(
	Integer id,
	Boolean isThumbnail,
	Short sequenceNumber,
	LocalDateTime createDatetime,
	LocalDateTime updateDatetime,
	String imageUrl,
	Boolean isDeleted
) {
}
