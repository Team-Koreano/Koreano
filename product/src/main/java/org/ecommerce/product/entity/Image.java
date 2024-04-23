package org.ecommerce.product.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "image")
@Getter
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "is_thumbnail")
	private Boolean isThumbnail;

	@Column(name = "sequence_number")
	private Short sequenceNumber;

	@CreationTimestamp
	@Column(name = "create_datetime", nullable = false, updatable = false)
	private LocalDateTime createDatetime;

	@Column(name = "update_datetime")
	private LocalDateTime updateDatetime;

	public static Image ofCreate(String imageUrl, Boolean isThumbnail, Short sequenceNumber, Product product){
		Image image = new Image();
		image.imageUrl = imageUrl;
		image.isThumbnail = isThumbnail;
		image.sequenceNumber = sequenceNumber;
		image.product = product;
		return image;
	}
}