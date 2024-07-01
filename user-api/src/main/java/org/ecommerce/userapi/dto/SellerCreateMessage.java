package org.ecommerce.userapi.dto;

import lombok.Getter;

@Getter
public class SellerCreateMessage {
	Integer sellerId;
	String bizName;

	public static SellerCreateMessage of(Integer sellerId, String bizName) {
		SellerCreateMessage sellerCreateMessage = new SellerCreateMessage();
		sellerCreateMessage.sellerId = sellerId;
		sellerCreateMessage.bizName = bizName;
		return sellerCreateMessage;
	}
}
