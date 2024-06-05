package org.ecommerce.productmanagementapi.dto.response;

public record ImageResponse(
	String imageUrl,
	Short sequenceNumber,
	boolean isThumbnail
) {
}
