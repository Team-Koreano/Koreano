package org.ecommerce.productapi.dto;

import java.util.List;

import org.ecommerce.productapi.document.ProductDocument;
import org.ecommerce.productapi.entity.Product;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.ecommerce.productapi.dto.response.ProductResponse;
import org.ecommerce.productapi.dto.response.CategoryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {
	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

	@Mapping(target = "bizName", source = "sellerRep.bizName")
	@Mapping(target = "categoryResponse", source = ".", qualifiedByName = "mapCategoryResponse")
	ProductResponse toResponse(ProductWithSellerRepAndImagesDto dto);

	List<ProductResponse> toResponse(List<ProductWithSellerRepAndImagesDto> dtos);

	ProductWithSellerRepAndImagesDto toDto(Product product);

	// List<ProductWithSellerRepAndImagesDto> toDtos(List<Product> products);

	@Mapping(source = "sellerId", target = "sellerRep.id")
	@Mapping(source = "sellerName", target = "sellerRep.bizName")
	ProductDto documentToDto(ProductDocument productDocument);

	@Mapping(target = "images", source = "images")
	ProductWithSellerRepAndImagesDto entityToDtoWithImageList(Product product);

	@Mapping(source = "sellerId", target = "sellerRep.id")
	@Mapping(source = "sellerName", target = "sellerRep.bizName")
	ProductWithSellerRepAndImagesDto documentToDtoWithImageList(ProductDocument productDocument);

	@Named("mapCategoryResponse")
	default CategoryResponse mapCategoryResponse(ProductWithSellerRepAndImagesDto dto) {
		if (dto.category() == ProductCategory.BEAN) {
			return new CategoryResponse.BeanResponse(
				dto.isDecaf(),
				dto.acidity().name(),
				dto.bean().name(),
				dto.isCrush());
		} else {
			return new CategoryResponse.DefaultResponse(
				dto.size(),
				dto.capacity()
			);
		}
	}
}
