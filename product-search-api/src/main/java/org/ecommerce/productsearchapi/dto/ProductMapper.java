package org.ecommerce.productsearchapi.dto;

import org.ecommerce.product.entity.Product;
import org.ecommerce.productsearchapi.document.ProductDocument;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

	ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);


	@Mapping(target = "imageDtoList", source = "images")
	ProductSearchDto entityToDto(Product product);

	ProductSearchDto documentToDto(ProductDocument productDocument);


}
