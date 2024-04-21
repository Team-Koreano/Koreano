package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.type.UserStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-19T00:59:12+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class SellerMapperImpl implements SellerMapper {

    @Override
    public SellerDto toDto(Seller users) {
        if ( users == null ) {
            return null;
        }

        Integer id = null;
        String email = null;
        String name = null;
        String password = null;
        String address = null;
        String phoneNumber = null;
        LocalDateTime createDatetime = null;
        Boolean isDeleted = null;
        LocalDateTime updateDatetime = null;
        Integer beanPay = null;
        UserStatus userStatus = null;

        id = users.getId();
        email = users.getEmail();
        name = users.getName();
        password = users.getPassword();
        address = users.getAddress();
        phoneNumber = users.getPhoneNumber();
        createDatetime = users.getCreateDatetime();
        isDeleted = users.getIsDeleted();
        updateDatetime = users.getUpdateDatetime();
        beanPay = users.getBeanPay();
        userStatus = users.getUserStatus();

        String accessToken = null;

        SellerDto sellerDto = new SellerDto( id, email, name, password, address, phoneNumber, createDatetime, isDeleted, updateDatetime, beanPay, userStatus, accessToken );

        return sellerDto;
    }

    @Override
    public SellerDto fromAccessToken(String accessToken) {
        if ( accessToken == null ) {
            return null;
        }

        String accessToken1 = null;

        accessToken1 = accessToken;

        Integer id = null;
        String email = null;
        String name = null;
        String password = null;
        String address = null;
        String phoneNumber = null;
        LocalDateTime createDatetime = null;
        Boolean isDeleted = null;
        LocalDateTime updateDatetime = null;
        Integer beanPay = null;
        UserStatus userStatus = null;

        SellerDto sellerDto = new SellerDto( id, email, name, password, address, phoneNumber, createDatetime, isDeleted, updateDatetime, beanPay, userStatus, accessToken1 );

        return sellerDto;
    }
}
