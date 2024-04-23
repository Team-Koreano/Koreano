package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.userapi.entity.Address;
import org.ecommerce.userapi.entity.Users;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-22T21:59:40+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class AddressMapperImpl implements AddressMapper {

    @Override
    public AddressDto toDto(Address address) {
        if ( address == null ) {
            return null;
        }

        Integer id = null;
        Users users = null;
        String name = null;
        String postAddress = null;
        String detail = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;

        id = address.getId();
        users = address.getUsers();
        name = address.getName();
        postAddress = address.getPostAddress();
        detail = address.getDetail();
        createDatetime = address.getCreateDatetime();
        updateDatetime = address.getUpdateDatetime();

        boolean isDeleted = false;

        AddressDto addressDto = new AddressDto( id, users, name, postAddress, detail, createDatetime, isDeleted, updateDatetime );

        return addressDto;
    }
}
