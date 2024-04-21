package org.ecommerce.orderapi.dto;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-20T19:57:18+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDto responseToDto(ProductDto.Response response) {
        if ( response == null ) {
            return null;
        }

        Integer id = null;
        Integer price = null;
        Integer stock = null;
        String seller = null;

        id = response.id();
        price = response.price();
        stock = response.stock();
        seller = response.seller();

        ProductDto productDto = new ProductDto( id, price, stock, seller );

        return productDto;
    }
}
