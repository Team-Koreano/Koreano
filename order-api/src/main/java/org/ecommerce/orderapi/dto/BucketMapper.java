package org.ecommerce.orderapi.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BucketMapper {

	BucketMapper INSTANCE = Mappers.getMapper(BucketMapper.class);

	BucketDto responseToDto(BucketDto.Response response);
}
