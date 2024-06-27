package org.ecommerce.productapi.dto.response;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.ecommerce.productapi.dto.ImageDto;
import org.ecommerce.productapi.dto.ProductWithSellerRepAndImagesAndProductDetailsDto;

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
	public static DetailResponse of(
		final ProductWithSellerRepAndImagesAndProductDetailsDto productWithSellerRepAndImagesAndProductDetailsDto) {
		return new DetailResponse(
			productWithSellerRepAndImagesAndProductDetailsDto.id(),
			productWithSellerRepAndImagesAndProductDetailsDto.isDecaf(),
			productWithSellerRepAndImagesAndProductDetailsDto.price(),
			productWithSellerRepAndImagesAndProductDetailsDto.sellerRep().id(),
			productWithSellerRepAndImagesAndProductDetailsDto.sellerRep().bizName(),
			productWithSellerRepAndImagesAndProductDetailsDto.stock(),
			productWithSellerRepAndImagesAndProductDetailsDto.acidity().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.bean().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.category().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.information(),
			productWithSellerRepAndImagesAndProductDetailsDto.name(),
			productWithSellerRepAndImagesAndProductDetailsDto.status().getTitle(),
			productWithSellerRepAndImagesAndProductDetailsDto.isCrush(),
			productWithSellerRepAndImagesAndProductDetailsDto.favoriteCount(),
			productWithSellerRepAndImagesAndProductDetailsDto.deliveryFee(),
			productWithSellerRepAndImagesAndProductDetailsDto.createDatetime(),
			productWithSellerRepAndImagesAndProductDetailsDto.images()
				.stream()
				.sorted(Comparator.comparingInt(ImageDto::sequenceNumber))
				.collect(Collectors.toCollection(LinkedList::new))
		);
	}
}
