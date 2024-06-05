package org.ecommerce.productmanagementapi.dto;

public record ImageDto(
	String imageUrl,
	Short sequenceNumber,
	boolean isThumbnail
) {
	public static ImageDto ofCreate(String imageUrl, Short sequenceNumber, boolean isThumbnail) {
		return new ImageDto(imageUrl, sequenceNumber, isThumbnail);
	}
}
