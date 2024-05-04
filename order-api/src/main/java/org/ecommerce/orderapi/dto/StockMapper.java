package org.ecommerce.orderapi.dto;

import org.ecommerce.orderapi.entity.Stock;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StockMapper {

	StockMapper INSTANCE = Mappers.getMapper(StockMapper.class);
	StockDto toStockDto(Stock stock);
	StockDto.Response toResponse(StockDto stockDto);
}
