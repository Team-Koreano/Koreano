package org.ecommerce.productsearchapi.document;

import java.time.LocalDateTime;

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

	@Field(type = FieldType.Integer, index = false)
	private Integer stock;

	@Field(type = FieldType.Integer, index = false)
	private Integer sellerId;

	@Field(type = FieldType.Text)
	private String sellerName;

	@Field(type = FieldType.Integer)
	private Integer favoriteCount;

	@Field(type = FieldType.Boolean)
	private Boolean isDecaf;

	@Field(type = FieldType.Text, analyzer = "cjk")
	private String name;

	@Field(type = FieldType.Text)
	private String acidity;

	@Field(type = FieldType.Text)
	private String bean;

	@Field(type = FieldType.Text, index = false)
	private String information;

	@Field(type = FieldType.Text)
	private String thumbnailUrl;

	@Field(type = FieldType.Text)
	private String size;

	@Field(type = FieldType.Text)
	private String capacity;

	@Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
	private LocalDateTime createDatetime;

	public static ProductDocument of(Product product) {
		ProductDocument productDocument = new ProductDocument();
		productDocument.id = product.getId();
		productDocument.category = product.getCategory().getCode();
		productDocument.sellerId = product.getSellerRep().getId();
		productDocument.sellerName = product.getSellerRep().getBizName();
		productDocument.favoriteCount = product.getFavoriteCount();
		productDocument.isDecaf = product.getIsDecaf();
		productDocument.name = product.getName();
		productDocument.acidity = product.getAcidity().getCode();
		productDocument.bean = product.getBean().getCode();
		productDocument.information = product.getInformation();
		productDocument.createDatetime = product.getCreateDatetime();
		productDocument.thumbnailUrl = product.getThumbnailUrl();
		productDocument.capacity = product.getCapacity();
		return productDocument;
	}

}
