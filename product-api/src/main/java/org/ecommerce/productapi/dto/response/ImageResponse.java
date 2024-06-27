package org.ecommerce.productapi.dto.response;

public record ImageResponse(
	String imageUrl,
	Short sequenceNumber,
	boolean isThumbnail
) {
}
