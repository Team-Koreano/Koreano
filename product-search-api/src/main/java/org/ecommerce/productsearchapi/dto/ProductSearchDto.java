package org.ecommerce.productsearchapi.dto;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;

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
	private LocalDateTime createDateTime;
	private LocalDateTime updateDateTime;
	private List<ImageDto> imageDtoList;


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
			LocalDateTime createDateTime,
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
					productSearchDto.getCreateDateTime(),
					productSearchDto.getImageDtoList()
				);
			}
		}
	}
}
