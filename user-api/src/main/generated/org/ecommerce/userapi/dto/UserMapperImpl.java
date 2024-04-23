package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.type.Gender;
import org.ecommerce.userapi.entity.type.UserStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-22T21:59:39+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserDto toDto(Users seller) {
        if ( seller == null ) {
            return null;
        }

        Integer id = null;
        String email = null;
        String name = null;
        String password = null;
        Gender gender = null;
        Short age = null;
        String phoneNumber = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;
        Integer beanPay = null;
        UserStatus userStatus = null;

        id = seller.getId();
        email = seller.getEmail();
        name = seller.getName();
        password = seller.getPassword();
        gender = seller.getGender();
        age = seller.getAge();
        phoneNumber = seller.getPhoneNumber();
        createDatetime = seller.getCreateDatetime();
        updateDatetime = seller.getUpdateDatetime();
        beanPay = seller.getBeanPay();
        userStatus = seller.getUserStatus();

        boolean isDeleted = false;
        String accessToken = null;

        UserDto userDto = new UserDto( id, email, name, password, gender, age, phoneNumber, createDatetime, isDeleted, updateDatetime, beanPay, userStatus, accessToken );

        return userDto;
    }

    @Override
    public UserDto fromAccessToken(String accessToken) {
        if ( accessToken == null ) {
            return null;
        }

        String accessToken1 = null;

        accessToken1 = accessToken;

        Integer id = null;
        String email = null;
        String name = null;
        String password = null;
        Gender gender = null;
        Short age = null;
        String phoneNumber = null;
        LocalDateTime createDatetime = null;
        boolean isDeleted = false;
        LocalDateTime updateDatetime = null;
        Integer beanPay = null;
        UserStatus userStatus = null;

        UserDto userDto = new UserDto( id, email, name, password, gender, age, phoneNumber, createDatetime, isDeleted, updateDatetime, beanPay, userStatus, accessToken1 );

        return userDto;
    }
}
