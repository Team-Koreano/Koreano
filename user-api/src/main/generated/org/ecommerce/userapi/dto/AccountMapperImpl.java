package org.ecommerce.userapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.userapi.entity.Seller;
import org.ecommerce.userapi.entity.SellerAccount;
import org.ecommerce.userapi.entity.Users;
import org.ecommerce.userapi.entity.UsersAccount;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-22T21:59:40+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class AccountMapperImpl implements AccountMapper {

    @Override
    public AccountDto toDto(SellerAccount sellerAccount) {
        if ( sellerAccount == null ) {
            return null;
        }

        Integer id = null;
        Seller seller = null;
        String number = null;
        String bankName = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;

        id = sellerAccount.getId();
        seller = sellerAccount.getSeller();
        number = sellerAccount.getNumber();
        bankName = sellerAccount.getBankName();
        createDatetime = sellerAccount.getCreateDatetime();
        updateDatetime = sellerAccount.getUpdateDatetime();

        Users users = null;
        boolean isDeleted = false;

        AccountDto accountDto = new AccountDto( id, seller, users, number, bankName, createDatetime, isDeleted, updateDatetime );

        return accountDto;
    }

    @Override
    public AccountDto toDto(UsersAccount usersAccount) {
        if ( usersAccount == null ) {
            return null;
        }

        Integer id = null;
        Users users = null;
        String number = null;
        String bankName = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;

        id = usersAccount.getId();
        users = usersAccount.getUsers();
        number = usersAccount.getNumber();
        bankName = usersAccount.getBankName();
        createDatetime = usersAccount.getCreateDatetime();
        updateDatetime = usersAccount.getUpdateDatetime();

        Seller seller = null;
        boolean isDeleted = false;

        AccountDto accountDto = new AccountDto( id, seller, users, number, bankName, createDatetime, isDeleted, updateDatetime );

        return accountDto;
    }
}
