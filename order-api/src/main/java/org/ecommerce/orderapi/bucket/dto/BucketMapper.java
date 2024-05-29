package org.ecommerce.orderapi.bucket.dto;

import org.ecommerce.orderapi.bucket.dto.response.BucketResponse;
import org.ecommerce.orderapi.bucket.entity.Bucket;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BucketMapper {

	BucketMapper INSTANCE = Mappers.getMapper(BucketMapper.class);

	BucketDto toDto(Bucket bucket);

	BucketResponse toResponse(BucketDto bucketDto);
}
