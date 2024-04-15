package org.ecommerce.bucketapi.dto;

import org.ecommerce.bucketapi.entity.Bucket;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BucketMapper {

	BucketMapper INSTANCE = Mappers.getMapper(BucketMapper.class);
	BucketDto toDto(Bucket bucket);
}