package org.ecommerce.productmanagementapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.product.entity.Product;
import org.ecommerce.product.entity.SellerRep;
import org.ecommerce.product.entity.enumerated.Acidity;
import org.ecommerce.product.entity.enumerated.Bean;
import org.ecommerce.product.entity.enumerated.ProductCategory;
import org.ecommerce.product.entity.enumerated.ProductStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-06-05T20:55:42+0900",
    comments = "version: 1.5.3.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-8.4.jar, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class ProductManagementMapperImpl implements ProductManagementMapper {

    @Override
    public ProductResponse toResponse(ProductWithSellerRepAndImagesDto dto) {
        if ( dto == null ) {
            return null;
        }

        String bizName = null;
        CategoryResponse categoryResponse = null;
        Integer id = null;
        Integer price = null;
        Integer stock = null;
        Integer favoriteCount = null;
        String category = null;
        String name = null;
        String status = null;
        String information = null;
        LocalDateTime createDatetime = null;
        Integer deliveryFee = null;
        List<ImageResponse> images = null;

        bizName = dtoSellerRepBizName( dto );
        categoryResponse = mapCategoryResponse( dto );
        id = dto.id();
        price = dto.price();
        stock = dto.stock();
        favoriteCount = dto.favoriteCount();
        if ( dto.category() != null ) {
            category = dto.category().name();
        }
        name = dto.name();
        if ( dto.status() != null ) {
            status = dto.status().name();
        }
        information = dto.information();
        createDatetime = dto.createDatetime();
        deliveryFee = dto.deliveryFee();
        images = imageDtoListToImageResponseList( dto.images() );

        ProductResponse productResponse = new ProductResponse( id, price, bizName, stock, favoriteCount, category, name, status, information, createDatetime, deliveryFee, images, categoryResponse );

        return productResponse;
    }

    @Override
    public List<ProductResponse> toResponse(List<ProductWithSellerRepAndImagesDto> dtos) {
        if ( dtos == null ) {
            return null;
        }

        List<ProductResponse> list = new ArrayList<ProductResponse>( dtos.size() );
        for ( ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto : dtos ) {
            list.add( toResponse( productWithSellerRepAndImagesDto ) );
        }

        return list;
    }

    @Override
    public ProductWithSellerRepAndImagesDto toDto(Product product) {
        if ( product == null ) {
            return null;
        }

        Integer id = null;
        ProductCategory category = null;
        SellerRepDto sellerRep = null;
        Integer favoriteCount = null;
        Boolean isDecaf = null;
        String name = null;
        Bean bean = null;
        Acidity acidity = null;
        String information = null;
        Boolean isCrush = null;
        String capacity = null;
        LocalDateTime createDatetime = null;
        LocalDateTime updateDatetime = null;
        Integer deliveryFee = null;
        List<ImageDto> images = null;

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
        capacity = product.getCapacity();
        createDatetime = product.getCreateDatetime();
        updateDatetime = product.getUpdateDatetime();
        if ( product.getDeliveryFee() != null ) {
            deliveryFee = product.getDeliveryFee().intValue();
        }
        images = imageListToImageDtoList( product.getImages() );

        Integer price = null;
        Integer stock = null;
        ProductStatus status = null;
        String size = null;

        ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto = new ProductWithSellerRepAndImagesDto( id, category, price, stock, sellerRep, favoriteCount, isDecaf, name, bean, acidity, information, isCrush, status, size, capacity, createDatetime, updateDatetime, deliveryFee, images );

        return productWithSellerRepAndImagesDto;
    }

    @Override
    public List<ProductWithSellerRepAndImagesDto> toDtos(List<Product> products) {
        if ( products == null ) {
            return null;
        }

        List<ProductWithSellerRepAndImagesDto> list = new ArrayList<ProductWithSellerRepAndImagesDto>( products.size() );
        for ( Product product : products ) {
            list.add( toDto( product ) );
        }

        return list;
    }

    private String dtoSellerRepBizName(ProductWithSellerRepAndImagesDto productWithSellerRepAndImagesDto) {
        if ( productWithSellerRepAndImagesDto == null ) {
            return null;
        }
        SellerRepDto sellerRep = productWithSellerRepAndImagesDto.sellerRep();
        if ( sellerRep == null ) {
            return null;
        }
        String bizName = sellerRep.bizName();
        if ( bizName == null ) {
            return null;
        }
        return bizName;
    }

    protected ImageResponse imageDtoToImageResponse(ImageDto imageDto) {
        if ( imageDto == null ) {
            return null;
        }

        String imageUrl = null;
        Short sequenceNumber = null;
        boolean isThumbnail = false;

        imageUrl = imageDto.imageUrl();
        sequenceNumber = imageDto.sequenceNumber();
        isThumbnail = imageDto.isThumbnail();

        ImageResponse imageResponse = new ImageResponse( imageUrl, sequenceNumber, isThumbnail );

        return imageResponse;
    }

    protected List<ImageResponse> imageDtoListToImageResponseList(List<ImageDto> list) {
        if ( list == null ) {
            return null;
        }

        List<ImageResponse> list1 = new ArrayList<ImageResponse>( list.size() );
        for ( ImageDto imageDto : list ) {
            list1.add( imageDtoToImageResponse( imageDto ) );
        }

        return list1;
    }

    protected SellerRepDto sellerRepToSellerRepDto(SellerRep sellerRep) {
        if ( sellerRep == null ) {
            return null;
        }

        Integer id = null;
        String bizName = null;

        id = sellerRep.getId();
        bizName = sellerRep.getBizName();

        SellerRepDto sellerRepDto = new SellerRepDto( id, bizName );

        return sellerRepDto;
    }

    protected ImageDto imageToImageDto(Image image) {
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

        ImageDto imageDto = new ImageDto( imageUrl, sequenceNumber, isThumbnail );

        return imageDto;
    }

    protected List<ImageDto> imageListToImageDtoList(List<Image> list) {
        if ( list == null ) {
            return null;
        }

        List<ImageDto> list1 = new ArrayList<ImageDto>( list.size() );
        for ( Image image : list ) {
            list1.add( imageToImageDto( image ) );
        }

        return list1;
    }
}
