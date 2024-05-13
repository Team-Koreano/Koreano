package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorMessages;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ProductManagementDto {
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
	private Boolean isCrush;
	private ProductStatus status;
	private String size;
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	private List<Image> images;

	public static class Request {
		public record Register(
			Boolean isDecaf,
			@NotNull(message = ProductManagementErrorMessages.priceNotNull)
			Integer price,
			@NotNull(message = ProductManagementErrorMessages.stockNotNull)
			Integer stock,
			Acidity acidity,
			Bean bean,
			ProductCategory category,
			@NotBlank(message = ProductManagementErrorMessages.informationNotBlank)
			String information,
			@NotBlank(message = ProductManagementErrorMessages.nameNotBlank)
			String name,
			Boolean isCrush,
			String size
		) {
		}

		public record Stock(
			Integer productId,
			@NotNull(message = ProductManagementErrorMessages.stockNotNull)
			Integer requestStock
		) {
		}

		public record Modify(
			Boolean isDecaf,
			@NotNull(message = ProductManagementErrorMessages.priceNotNull)
			Integer price,
			Acidity acidity,
			Bean bean,
			ProductCategory category,
			@NotBlank(message = ProductManagementErrorMessages.informationNotBlank)
			String information,
			@NotBlank(message = ProductManagementErrorMessages.nameNotBlank)
			String name,
			Boolean isCrush
		) {
		}

		public record Image(
			String imageUrl,
			Short sequenceNumber,
			boolean isThumbnail
		) {
			public static Image ofCreate(String imageUrl, Short sequenceNumber, boolean isThumbnail) {
				return new Image(imageUrl, sequenceNumber, isThumbnail);
			}
		}
	}

	public record Response(
		Integer id,
		Boolean isDecaf,
		Integer price,
		String bizName,
		Integer stock,
		Integer favoriteCount,
		String acidity,
		String bean,
		String category,
		String information,
		String name,
		String status,
		LocalDateTime createDatetime,
		Boolean isCrush,
		List<Image> images
	) {
		public record BeanProduct(
			Integer id,
			Boolean isDecaf,
			Integer price,
			String bizName,
			Integer stock,
			Integer favoriteCount,
			String acidity,
			String bean,
			String category,
			String information,
			String name,
			String status,
			LocalDateTime createDatetime,
			Boolean isCrush,
			String size,
			List<Image> images) {
		}
	}

	public record Image(
		String imageUrl,
		Short sequenceNumber,
		boolean isThumbnail) {
	}
}