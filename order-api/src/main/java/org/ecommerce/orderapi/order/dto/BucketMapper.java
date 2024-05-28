package org.ecommerce.orderapi.order.dto;

import org.ecommerce.orderapi.order.dto.response.BucketResponse;
import org.ecommerce.orderapi.order.entity.Bucket;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BucketMapper {

	BucketMapper INSTANCE = Mappers.getMapper(BucketMapper.class);

	Bucket responseToEntity(BucketResponse response);
}
