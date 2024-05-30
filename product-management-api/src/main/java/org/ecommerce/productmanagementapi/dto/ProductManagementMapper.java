package org.ecommerce.productmanagementapi.dto;

import java.util.List;

import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.productmanagementapi.dto.response.CategoryResponse;
import org.ecommerce.productmanagementapi.dto.response.ProductResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductManagementMapper {
	ProductManagementMapper INSTANCE = Mappers.getMapper(ProductManagementMapper.class);

	@Mapping(target = "bizName", source = "sellerRep.bizName")
	@Mapping(target = "categoryResponse", source = ".", qualifiedByName = "mapCategoryResponse")
	ProductResponse toResponse(ProductManagementDtoWithImages dto);

	List<ProductResponse> toResponse(List<ProductManagementDtoWithImages> dtos);

	ProductManagementDtoWithImages toDto(Product product);

	List<ProductManagementDtoWithImages> toDtos(List<Product> products);

	@Named("mapCategoryResponse")
	default CategoryResponse mapCategoryResponse(ProductManagementDtoWithImages dto) {
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
