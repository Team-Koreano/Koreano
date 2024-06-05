package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "charge_info")
public class ChargeInfo {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(length = 50)
	private String paymentKey;

	@Column(length = 50)
	private String payType;

	@Column(updatable = false)
	private LocalDateTime approveDateTime;

	public static ChargeInfo ofCharge(
		String paymentKey,
		String approveDateTime
	) {
		ChargeInfo chargeInfo = new ChargeInfo();
		chargeInfo.paymentKey = paymentKey;
		chargeInfo.approveDateTime = stringToDateTime(approveDateTime);
		return chargeInfo;
	}
}
