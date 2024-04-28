package org.ecommerce.productsearchapi.dto;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.document.ProductDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValueMappingStrategy = NullValueMappingStrategy.RETURN_NULL)
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


	@Mapping(target = "imageDtoList", source = "images")
	ProductSearchDto entityToDto(Product product);

	@Mapping(source = "sellerId", target = "sellerRep.id")
	@Mapping(source = "sellerName", target = "sellerRep.bizName")
	ProductSearchDto documentToDto(ProductDocument productDocument);


}
