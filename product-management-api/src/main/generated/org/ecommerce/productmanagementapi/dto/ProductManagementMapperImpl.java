package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.type.Acidity;
import org.ecommerce.product.entity.type.Bean;
import org.ecommerce.product.entity.type.ProductCategory;
import org.ecommerce.product.entity.type.ProductStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-21T22:32:05+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class ProductManagementMapperImpl implements ProductManagementMapper {

    @Override
    public ProductManagementDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        Integer id = null;
        ProductCategory category = null;
        Integer price = null;
        Integer stock = null;
        SellerRep sellerRep = null;
        Integer favoriteCount = null;
        Boolean isDecaf = null;
        String name = null;
        Bean bean = null;
        Acidity acidity = null;
        String information = null;
        Boolean isCrush = null;
        ProductStatus status = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;

        id = product.getId();
        category = product.getCategory();
        price = product.getPrice();
        stock = product.getStock();
        sellerRep = product.getSellerRep();
        favoriteCount = product.getFavoriteCount();
        isDecaf = product.getIsDecaf();
        name = product.getName();
        bean = product.getBean();
        acidity = product.getAcidity();
        information = product.getInformation();
        isCrush = product.getIsCrush();
        status = product.getStatus();
        createDatetime = product.getCreateDatetime();
        updateDatetime = product.getUpdateDatetime();

        ProductManagementDto productManagementDto = new ProductManagementDto( id, category, price, stock, sellerRep, favoriteCount, isDecaf, name, bean, acidity, information, isCrush, status, createDatetime, updateDatetime );

        return productManagementDto;
    }
}
