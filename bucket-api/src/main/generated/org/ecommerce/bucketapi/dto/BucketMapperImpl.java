package org.ecommerce.bucketapi.dto;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.ecommerce.bucketapi.entity.Bucket;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-21T20:10:09+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class BucketMapperImpl implements BucketMapper {

    @Override
    public BucketDto toDto(Bucket bucket) {
        if ( bucket == null ) {
            return null;
        }

        Long id = null;
        Integer userId = null;
        String seller = null;
        Integer productId = null;
        Integer quantity = null;
        LocalDate createDate = null;

        id = bucket.getId();
        userId = bucket.getUserId();
        seller = bucket.getSeller();
        productId = bucket.getProductId();
        quantity = bucket.getQuantity();
        createDate = bucket.getCreateDate();

        BucketDto bucketDto = new BucketDto( id, userId, seller, productId, quantity, createDate );

        return bucketDto;
    }

    @Override
    public BucketDto.Response toResponse(BucketDto bucketDto) {
        if ( bucketDto == null ) {
            return null;
        }

        Long id = null;
        Integer userId = null;
        String seller = null;
        Integer productId = null;
        Integer quantity = null;
        LocalDate createDate = null;

        id = bucketDto.getId();
        userId = bucketDto.getUserId();
        seller = bucketDto.getSeller();
        productId = bucketDto.getProductId();
        quantity = bucketDto.getQuantity();
        createDate = bucketDto.getCreateDate();

        BucketDto.Response response = new BucketDto.Response( id, userId, seller, productId, quantity, createDate );

        return response;
    }
}
