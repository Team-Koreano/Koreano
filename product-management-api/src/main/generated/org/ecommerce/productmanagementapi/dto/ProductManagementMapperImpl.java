package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-05-23T20:10:17+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class ProductManagementMapperImpl implements ProductManagementMapper {

    @Override
    public ProductManagementDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        List<ProductManagementDto.Image> images = null;
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
        String size = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;

        images = imageListToImageList( product.getImages() );
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
        size = product.getSize();
        createDatetime = product.getCreateDatetime();
        updateDatetime = product.getUpdateDatetime();

        ProductManagementDto productManagementDto = new ProductManagementDto( id, category, price, stock, sellerRep, favoriteCount, isDecaf, name, bean, acidity, information, isCrush, status, size, createDatetime, updateDatetime, images );

        return productManagementDto;
    }

    @Override
    public List<ProductManagementDto> productsToDtos(List<Product> product) {
        if ( product == null ) {
            return null;
        }

        List<ProductManagementDto> list = new ArrayList<ProductManagementDto>( product.size() );
        for ( Product product1 : product ) {
            list.add( toDto( product1 ) );
        }

        return list;
    }

    @Override
    public List<ProductManagementDto.Response> dtosToResponses(List<ProductManagementDto> productManagementDtos) {
        if ( productManagementDtos == null ) {
            return null;
        }

        List<ProductManagementDto.Response> list = new ArrayList<ProductManagementDto.Response>( productManagementDtos.size() );
        for ( ProductManagementDto productManagementDto : productManagementDtos ) {
            list.add( toResponse( productManagementDto ) );
        }

        return list;
    }

    protected ProductManagementDto.Image imageToImage(Image image) {
        if ( image == null ) {
            return null;
        }

        String imageUrl = null;
        Short sequenceNumber = null;
        boolean isThumbnail = false;

        imageUrl = image.getImageUrl();
        sequenceNumber = image.getSequenceNumber();
        if ( image.getIsThumbnail() != null ) {
            isThumbnail = image.getIsThumbnail();
        }

        ProductManagementDto.Image image1 = new ProductManagementDto.Image( imageUrl, sequenceNumber, isThumbnail );

        return image1;
    }

    protected List<ProductManagementDto.Image> imageListToImageList(List<Image> list) {
        if ( list == null ) {
            return null;
        }

        List<ProductManagementDto.Image> list1 = new ArrayList<ProductManagementDto.Image>( list.size() );
        for ( Image image : list ) {
            list1.add( imageToImage( image ) );
        }

        return list1;
    }
}
