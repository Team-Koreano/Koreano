package org.ecommerce.productmanagementapi.dto;

import java.util.List;

import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductManagementMapper {
	ProductManagementMapper INSTANCE = Mappers.getMapper(ProductManagementMapper.class);

	ProductManagementDto toDto(Product product);

	List<ProductManagementDto> productsToDtos(List<Product> product);

	default ProductManagementDto.Response toResponse(ProductManagementDto dto) {
		if (dto.getCategory() == ProductCategory.BEAN) {
			return mapBeanProduct(dto);
		} else {
			return mapDefaultProduct(dto);
		}
	}

	@Named("mapBeanProduct")
	default ProductManagementDto.Response.BeanProductResponse mapBeanProduct(ProductManagementDto dto) {
		return new ProductManagementDto.Response.BeanProductResponse(
			dto.getId(),
			dto.getPrice(),
			dto.getSellerRep().getBizName(),
			dto.getStock(),
			dto.getFavoriteCount(),
			dto.getCategory().name(),
			dto.getName(),
			dto.getStatus().name(),
			dto.getInformation(),
			dto.getCreateDatetime(),
			dto.getImages(),
			dto.getIsDecaf(),
			dto.getAcidity().name(),
			dto.getBean().name(),
			dto.getIsCrush()
		);
	}

	@Named("mapDefaultProduct")
	default ProductManagementDto.Response.DefaultProductResponse mapDefaultProduct(ProductManagementDto dto) {
		return new ProductManagementDto.Response.DefaultProductResponse(
			dto.getId(),
			dto.getPrice(),
			dto.getSellerRep().getBizName(),
			dto.getStock(),
			dto.getFavoriteCount(),
			dto.getCategory().name(),
			dto.getName(),
			dto.getStatus().name(),
			dto.getInformation(),
			dto.getCreateDatetime(),
			dto.getImages(),
			dto.getSize()
		);
	}

	List<ProductManagementDto.Response> dtosToResponses(List<ProductManagementDto> productManagementDtos);
}
