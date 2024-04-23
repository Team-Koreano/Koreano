package org.ecommerce.productsearchapi.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ImageDto {
	private Integer id;
	private Boolean isThumbnail;
	private Short sequenceNumber;
	private LocalDateTime createDateTime;
	private LocalDateTime updateDateTime;
	private String imageUrl;
	private boolean isDeleted;
}