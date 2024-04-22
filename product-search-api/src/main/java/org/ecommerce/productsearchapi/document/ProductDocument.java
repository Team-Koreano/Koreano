package org.ecommerce.productsearchapi.document;

import java.time.LocalDateTime;

import org.ecommerce.product.entity.Image;
import org.ecommerce.product.entity.Product;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Document(indexName = "product")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProductDocument {

	@Id
	@Field(type = FieldType.Integer)
	private Integer id;

	@Field(type = FieldType.Text)
	private String category;

	@Field(type = FieldType.Integer)
	private Integer price;

	@Field(type = FieldType.Integer)
	private Integer stock;

	@Field(type = FieldType.Integer)
	private Integer sellerId;

	@Field(type = FieldType.Text)
	private String sellerName;

	@Field(type = FieldType.Integer)
	private Integer favoriteCount;

	@Field(type = FieldType.Boolean)
	private Boolean isDecaf;

	@Field(type = FieldType.Text)
	private String name;

	@Field(type = FieldType.Text)
	private String acidity;

	@Field(type = FieldType.Text)
	private String bean;

	@Field(type = FieldType.Text)
	private String information;

	@Field(type = FieldType.Boolean)
	private Boolean isCrush;

	@Field(type = FieldType.Text)
	private String thumbnailUrl;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
	private LocalDateTime createDatetime;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
	private LocalDateTime updateDatetime;

	public static ProductDocument of(Product product, Image image) {
		ProductDocument productDocument = new ProductDocument();
		productDocument.id = product.getId();
		productDocument.category = product.getCategory().name();
		productDocument.price = product.getPrice();
		productDocument.stock = product.getStock();
		productDocument.sellerId = product.getSellerRep().getId();
		productDocument.sellerName = product.getSellerRep().getBizName();
		productDocument.favoriteCount = product.getFavoriteCount();
		productDocument.isDecaf = product.getIsDecaf();
		productDocument.name = product.getName();
		productDocument.acidity = product.getAcidity().name();
		productDocument.bean = product.getBean().name();
		productDocument.information = product.getInformation();
		productDocument.isCrush = product.getIsCrush();
		productDocument.createDatetime = product.getCreateDatetime();
		productDocument.updateDatetime = product.getUpdateDatetime();
		productDocument.thumbnailUrl = image.getImageUrl();
		return productDocument;
	}

}
