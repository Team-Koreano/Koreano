package org.ecommerce.paymentapi.dto;

import org.ecommerce.paymentapi.entity.BeanPayDetail;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BeanPayDetailMapper {

	BeanPayDetailMapper INSTANCE = Mappers.getMapper(BeanPayDetailMapper.class);
	BeanPayDetailDto toDto(BeanPayDetail beanPayDetail);

}