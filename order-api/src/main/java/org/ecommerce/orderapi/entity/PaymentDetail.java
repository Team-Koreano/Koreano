package org.ecommerce.orderapi.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PaymentDetail {
	@Id
	private UUID id;
	private Long orderItemId;
	private Integer deliveryFee;
	private Integer totalPrice;
	private Integer paymentAmount;
	private LocalDateTime paymentDatetime;
}
