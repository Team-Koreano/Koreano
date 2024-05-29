package org.ecommerce.productsearchapi.dto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.ecommerce.common.aop.ValidEnum;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productsearchapi.enumerated.ProductSortType;
import org.ecommerce.productsearchapi.exception.ProductSearchErrorMessages;
import org.springframework.util.StringUtils;

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
	private String size;
	private String capacity;

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
		public record Search(
			String keyword,
			Boolean isDecaf,
			@ValidEnum(enumClass = ProductCategory.class,
				nullable = true,
				message = ProductSearchErrorMessages.NOT_FOUND_CATEGORY)
			ProductCategory category,

			@ValidEnum(enumClass = Bean.class,
				nullable = true,
				message = ProductSearchErrorMessages.NOT_FOUND_BEAN)
			Bean bean,

			@ValidEnum(enumClass = Acidity.class,
				nullable = true,
				message = ProductSearchErrorMessages.NOT_FOUND_ACIDITY)
			Acidity acidity,

			@ValidEnum(enumClass = ProductSortType.class,
				nullable = true,
				message = ProductSearchErrorMessages.NOT_FOUND_SORT)
			ProductSortType sortType
		) {

			public boolean validKeyword() {
				return StringUtils.hasText(this.keyword);
			}

			public boolean validIsDecaf() {
				return this.isDecaf != null;
			}

			public boolean validCategory() {
				return this.category != null;
			}

			public boolean validBean() {
				return this.bean != null;
			}

			public boolean validAcidity() {
				return this.acidity != null;
			}

		}
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
			LinkedList<ImageDto> imageDtoList
		) {
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
						.stream()
						.sorted(Comparator.comparingInt(ImageDto::getSequenceNumber))
						.collect(Collectors.toCollection(LinkedList::new))
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
			String size,
			String capacity,
			LocalDateTime createDatetime
		) {
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
					productSearchDto.getSize(),
					productSearchDto.getCapacity(),
					productSearchDto.getCreateDatetime()
				);
			}
		}

		public record SuggestedProducts(
			Integer id,
			String name
		) {
			public static SuggestedProducts of(final ProductSearchDto productSearchDto) {
				return new SuggestedProducts(
					productSearchDto.getId(),
					productSearchDto.getName()
				);
			}
		}

		public record Search(
			Integer id,
			String name,
			String category,
			Integer price,
			Integer stock,
			Integer sellerId,
			String sellerName,
			Integer favoriteCount,
			Boolean isDecaf,
			String acidity,
			String bean,
			String thumbnailUrl,
			LocalDateTime createDatetime
		) {
			public static Search of(final ProductSearchDto productSearchDto) {
				return new Search(
					productSearchDto.getId(),
					productSearchDto.getName(),
					productSearchDto.getCategory().getTitle(),
					productSearchDto.getPrice(),
					productSearchDto.getStock(),
					productSearchDto.getSellerRep().getId(),
					productSearchDto.getSellerRep().getBizName(),
					productSearchDto.getFavoriteCount(),
					productSearchDto.getIsDecaf(),
					productSearchDto.getAcidity().getTitle(),
					productSearchDto.getBean().getTitle(),
					productSearchDto.getThumbnailUrl(),
					productSearchDto.getCreateDatetime()
				);
			}

		}
	}
}
