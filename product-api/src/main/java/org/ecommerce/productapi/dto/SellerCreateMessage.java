package org.ecommerce.productapi.dto;

import lombok.Getter;

@Getter
public class SellerCreateMessage {
	private Integer sellerId;
	private String bizName;

	public static SellerCreateMessage of(Integer sellerId, String bizName) {
		SellerCreateMessage sellerCreateMessage = new SellerCreateMessage();
		sellerCreateMessage.sellerId = sellerId;
		sellerCreateMessage.bizName = bizName;
		return sellerCreateMessage;
	}
}
