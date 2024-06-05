package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.ecommerce.productapi.dto.ImageDto;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesDto;

public record DetailResponse(
	Integer id,
	Boolean isDecaf,
	Integer price,
	Integer sellerId,
	String sellerName,
	Integer stock,
	String acidity,
	String bean,
	String category,
	String information,
	String name,
	String status,
	Boolean isCrush,
	Integer favoriteCount,
	Short deliveryFee,
	LocalDateTime createDatetime,
	LinkedList<ImageDto> imageDtoList
) {
	public static DetailResponse of(final ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto) {
		return new DetailResponse(
			productWithSellerRepAndImagesDto.id(),
			productWithSellerRepAndImagesDto.isDecaf(),
			productWithSellerRepAndImagesDto.price(),
			productWithSellerRepAndImagesDto.sellerRep().id(),
			productWithSellerRepAndImagesDto.sellerRep().bizName(),
			productWithSellerRepAndImagesDto.stock(),
			productWithSellerRepAndImagesDto.acidity().getTitle(),
			productWithSellerRepAndImagesDto.bean().getTitle(),
			productWithSellerRepAndImagesDto.category().getTitle(),
			productWithSellerRepAndImagesDto.information(),
			productWithSellerRepAndImagesDto.name(),
			productWithSellerRepAndImagesDto.status().getTitle(),
			productWithSellerRepAndImagesDto.isCrush(),
			productWithSellerRepAndImagesDto.favoriteCount(),
			productWithSellerRepAndImagesDto.deliveryFee(),
			productWithSellerRepAndImagesDto.createDatetime(),
			productWithSellerRepAndImagesDto.images()
				.stream()
				.sorted(Comparator.comparingInt(ImageDto::sequenceNumber))
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}
}
