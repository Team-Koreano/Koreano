package org.ecommerce.orderapi.dto;

import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-20T19:57:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class BucketMapperImpl implements BucketMapper {

    @Override
    public BucketDto responseToDto(BucketDto.Response response) {
        if ( response == null ) {
            return null;
        }

        Long id = null;
        Integer userId = null;
        String seller = null;
        Integer productId = null;
        Integer quantity = null;
        LocalDate createDate = null;

        id = response.id();
        userId = response.userId();
        seller = response.seller();
        productId = response.productId();
        quantity = response.quantity();
        createDate = response.createDate();

        BucketDto bucketDto = new BucketDto( id, userId, seller, productId, quantity, createDate );

        return bucketDto;
    }
}
