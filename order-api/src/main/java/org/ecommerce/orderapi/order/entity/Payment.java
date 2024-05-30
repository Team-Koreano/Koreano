package org.ecommerce.orderapi.order.entity;

import java.time.LocalDateTime;
import java.util.Map;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class Payment {
	@Id
	private Long id;
	private Integer totalPaymentAmount;
	private LocalDateTime paymentDatetime;
	@OneToMany
	private Map<Long, PaymentDetail> paymentDetails;
}
