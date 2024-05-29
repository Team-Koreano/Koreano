package org.ecommerce.orderapi.order.entity;

import org.ecommerce.orderapi.order.entity.enumerated.ProductStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
	@Id
	private Integer id;
	private String name;
	private Integer price;
	private Integer deliveryFee;
	private Integer sellerId;
	private String sellerName;
	private ProductStatus status;

	public boolean isAvailableStatus() {
		return status == ProductStatus.AVAILABLE;
	}
}
