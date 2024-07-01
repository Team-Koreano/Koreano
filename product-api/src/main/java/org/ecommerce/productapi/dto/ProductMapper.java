package org.ecommerce.productapi.dto;

import java.util.List;

import org.ecommerce.productapi.document.ProductDocument;
import org.ecommerce.productapi.dto.response.CategoryResponse;
import org.ecommerce.productapi.dto.response.ProductDetailResponse;
import org.ecommerce.productapi.dto.response.ProductResponse;
import org.ecommerce.productapi.entity.Product;
import org.ecommerce.productapi.entity.ProductDetail;
import org.ecommerce.productapi.entity.enumerated.ProductCategory;
import org.mapstruct.IterableMapping;
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
	ProductResponse toResponse(ProductWithSellerRepAndImagesAndProductDetailsDto dto);

	ProductDetailResponse toResponse(ProductDetailDto productDetailDto);

	List<ProductResponse> toResponse(List<ProductWithSellerRepAndImagesAndProductDetailsDto> dtos);

	@Named(value = "entityToDto")
	ProductWithSellerRepAndImagesAndProductDetailsDto toDto(Product product);

	@Mapping(source = "id", target = "productDetailId")
	ProductDetailDto toDto(ProductDetail productDetail);

	@IterableMapping(qualifiedByName = "entityToDto")
	List<ProductWithSellerRepAndImagesAndProductDetailsDto> toDtos(List<Product> products);

	@Mapping(source = "sellerId", target = "sellerRep.id")
	@Mapping(source = "sellerName", target = "sellerRep.bizName")
	ProductDto documentToDto(ProductDocument productDocument);

	@Mapping(target = "images", source = "images")
	ProductWithSellerRepAndImagesAndProductDetailsDto entityToDtoWithImageList(Product product);

	@Mapping(source = "sellerId", target = "sellerRep.id")
	@Mapping(source = "sellerName", target = "sellerRep.bizName")
	ProductWithSellerRepAndImagesAndProductDetailsDto documentToDtoWithImageList(ProductDocument productDocument);

	@Named("mapCategoryResponse")
	default CategoryResponse mapCategoryResponse(ProductWithSellerRepAndImagesAndProductDetailsDto dto) {
		if (dto.category() == ProductCategory.BEAN) {
			return new CategoryResponse.BeanResponse(
				dto.isDecaf(),
				dto.acidity().name(),
				dto.bean().name(),
				dto.isCrush());
		} else {
			return new CategoryResponse.DefaultResponse(
				dto.capacity()
			);
		}
	}
}
