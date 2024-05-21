package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.ecommerce.productmanagementapi.exception.ProductManagementErrorMessages;

import jakarta.validation.constraints.Min;
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
	private String capacity;
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	private List<Image> images;

	public static class Request {
		public record Register(
			Boolean isDecaf,
			@NotNull(message = ProductManagementErrorMessages.priceNotNull)
			@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
			Integer price,
			@NotNull(message = ProductManagementErrorMessages.stockNotNull)
			@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
			Integer stock,
			Acidity acidity,
			Bean bean,
			ProductCategory category,
			@NotBlank(message = ProductManagementErrorMessages.informationNotBlank)
			String information,
			@NotBlank(message = ProductManagementErrorMessages.nameNotBlank)
			String name,
			Boolean isCrush,
			String size,
			String capacity
		) {
		}

		public record Stock(
			Integer productId,
			@NotNull(message = ProductManagementErrorMessages.stockNotNull)
			@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
			Integer requestStock
		) {
		}

		public record Modify(
			Boolean isDecaf,
			@NotNull(message = ProductManagementErrorMessages.priceNotNull)
			@Min(value = 0, message = ProductManagementErrorMessages.isCanNotBeBelowZero)
			Integer price,
			Acidity acidity,
			Bean bean,
			ProductCategory category,
			@NotBlank(message = ProductManagementErrorMessages.informationNotBlank)
			String information,
			@NotBlank(message = ProductManagementErrorMessages.nameNotBlank)
			String name,
			String size,
			String capacity,
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

		public record BulkStatus(
			List<Integer> productId,
			ProductStatus productStatus
		) {
		}
	}

	@Getter
	@AllArgsConstructor
	public static class Response {
		private Integer id;
		private Integer price;
		private String bizName;
		private Integer stock;
		private Integer favoriteCount;
		private String category;
		private String name;
		private String status;
		private String information;
		private LocalDateTime createDatetime;
		private List<Image> images;

		@Getter
		public static class BeanProductResponse extends Response {
			private final Boolean isDecaf;
			private final String acidity;
			private final String bean;
			private final Boolean isCrush;

			public BeanProductResponse(Integer id, Integer price, String bizName, Integer stock, Integer favoriteCount,
				String category, String name, String status, String information, LocalDateTime createDatetime,
				List<Image> images,
				Boolean isDecaf, String acidity, String bean, Boolean isCrush) {
				super(id, price, bizName, stock, favoriteCount, category, name, status, information, createDatetime,
					images);
				this.isDecaf = isDecaf;
				this.acidity = acidity;
				this.bean = bean;
				this.isCrush = isCrush;
			}
		}

		@Getter
		public static class DefaultProductResponse extends Response {
			private final String size;
			private final String capacity;

			public DefaultProductResponse(Integer id, Integer price, String bizName, Integer stock,
				Integer favoriteCount, String category, String name, String status, String information,
				LocalDateTime createDatetime,
				List<Image> images, String size, String capacity) {
				super(id, price, bizName, stock, favoriteCount, category, name, status, information, createDatetime,
					images);
				this.size = size;
				this.capacity = capacity;
			}
		}
	}

	public record Image(
		String imageUrl,
		Short sequenceNumber,
		boolean isThumbnail
	) {
	}
}