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
    date = "2024-04-26T20:52:40+0900",
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

    @Override
    public ProductManagementDto.Response toResponse(ProductManagementDto productManagementDto) {
        if ( productManagementDto == null ) {
            return null;
        }

        Integer id = null;
        Boolean isDecaf = null;
        Integer price = null;
        Integer stock = null;
        Integer favoriteCount = null;
        String acidity = null;
        String bean = null;
        String category = null;
        String information = null;
        String name = null;
        String status = null;
        LocalDateTime createDatetime = null;
        Boolean isCrush = null;

        id = productManagementDto.getId();
        isDecaf = productManagementDto.getIsDecaf();
        price = productManagementDto.getPrice();
        stock = productManagementDto.getStock();
        favoriteCount = productManagementDto.getFavoriteCount();
        if ( productManagementDto.getAcidity() != null ) {
            acidity = productManagementDto.getAcidity().name();
        }
        if ( productManagementDto.getBean() != null ) {
            bean = productManagementDto.getBean().name();
        }
        if ( productManagementDto.getCategory() != null ) {
            category = productManagementDto.getCategory().name();
        }
        information = productManagementDto.getInformation();
        name = productManagementDto.getName();
        if ( productManagementDto.getStatus() != null ) {
            status = productManagementDto.getStatus().name();
        }
        createDatetime = productManagementDto.getCreateDatetime();
        isCrush = productManagementDto.getIsCrush();

        String bizName = null;

        ProductManagementDto.Response response = new ProductManagementDto.Response( id, isDecaf, price, bizName, stock, favoriteCount, acidity, bean, category, information, name, status, createDatetime, isCrush );

        return response;
    }
}
