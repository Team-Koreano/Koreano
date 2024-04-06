package org.ecommerce.productsearchapi.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

	@ManyToOne
	@JoinColumn(name = "product_id")
	private Product product;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "is_thumbnail")
	private Boolean isThumbnail;

	@CreationTimestamp
	@Column(name = "create_datetime", nullable = false, updatable = false)
	private LocalDateTime createDatetime;

	@Column(name = "update_date", insertable = false)
	private LocalDateTime updateDatetime;

}
