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
	private LocalDateTime createDatetime;
	private LocalDateTime updateDatetime;
	private String imageUrl;
	private Boolean isDeleted;

}