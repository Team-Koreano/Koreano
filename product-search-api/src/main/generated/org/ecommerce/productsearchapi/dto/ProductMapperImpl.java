package org.ecommerce.productsearchapi.dto;

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
    date = "2024-04-21T23:12:01+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductSearchDto toDto(Product product) {
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
        ProductStatus status = null;
        Boolean isCrush = null;

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
        status = product.getStatus();
        isCrush = product.getIsCrush();

        LocalDateTime createDateTime = null;
        LocalDateTime updateDateTime = null;

        ProductSearchDto productSearchDto = new ProductSearchDto( id, category, price, stock, sellerRep, favoriteCount, isDecaf, name, bean, acidity, information, status, isCrush, createDateTime, updateDateTime );

        return productSearchDto;
    }
}
