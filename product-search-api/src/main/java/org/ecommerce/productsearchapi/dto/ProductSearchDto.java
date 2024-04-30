package org.ecommerce.productsearchapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;

import com.google.common.annotations.VisibleForTesting;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProductSearchDto {

	private Integer id;
	private ProductCategory category;
	private Integer price;
	private Integer stock;
	private SellerRep sellerRep;
	private Integer favoriteCount;
	private Boolean isDecaf;
	private String name;
	private Bean bean;
	private Acidity acidity;
	private String information;
	private ProductStatus status;
	private Boolean isCrush;
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	private List<ImageDto> imageDtoList;
	private String thumbnailUrl;

	@Getter
	@AllArgsConstructor
	public static class SellerRep {
		private Integer id;
		private String bizName;
	}

	@Getter
	@AllArgsConstructor
	public static class ImageDto {
		private Integer id;
		private Boolean isThumbnail;
		private Short sequenceNumber;
		private LocalDateTime createDatetime;
		private LocalDateTime updateDatetime;
		private String imageUrl;
		private Boolean isDeleted;
	}


	public static class Request {

	}
	public static class Response {
		public record Detail(
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
			LocalDateTime createDatetime,
			List<ImageDto> imageDtoList
		){
			public static Detail of(final ProductSearchDto productSearchDto) {
				return new Detail(
					productSearchDto.getId(),
					productSearchDto.getIsDecaf(),
					productSearchDto.getPrice(),
					productSearchDto.getSellerRep().getId(),
					productSearchDto.getSellerRep().getBizName(),
					productSearchDto.getStock(),
					productSearchDto.getAcidity().getTitle(),
					productSearchDto.getBean().getTitle(),
					productSearchDto.getCategory().getTitle(),
					productSearchDto.getInformation(),
					productSearchDto.getName(),
					productSearchDto.getStatus().getTitle(),
					productSearchDto.getIsCrush(),
					productSearchDto.getFavoriteCount(),
					productSearchDto.getCreateDatetime(),
					productSearchDto.getImageDtoList()
				);
			}
		}
		public record SavedProduct(
			Integer id,
			String category,
			Integer price,
			Integer stock,
			Integer sellerId,
			String sellerName,
			Integer favoriteCount,
			Boolean isDecaf,
			String name,
			String acidity,
			String bean,
			String information,
			String thumbnailUrl,
			LocalDateTime createDatetime
		){
			@VisibleForTesting
			public static String getThumbnailUrl(List<ImageDto> images) {
				return images.stream()
					.filter(ImageDto::getIsThumbnail)
					.findFirst()
					.map(ImageDto::getImageUrl)
					.orElse(null);
			}


			public static SavedProduct of(final ProductSearchDto productSearchDto) {
				return new SavedProduct(
					productSearchDto.getId(),
					productSearchDto.getCategory().getTitle(),
					productSearchDto.getPrice(),
					productSearchDto.getStock(),
					productSearchDto.getSellerRep().getId(),
					productSearchDto.getSellerRep().getBizName(),
					productSearchDto.getFavoriteCount(),
					productSearchDto.getIsDecaf(),
					productSearchDto.getName(),
					productSearchDto.getAcidity().getTitle(),
					productSearchDto.getBean().getTitle(),
					productSearchDto.getInformation(),
					productSearchDto.getThumbnailUrl(),
					productSearchDto.getCreateDatetime()
				);
			}
		}
	}
}
