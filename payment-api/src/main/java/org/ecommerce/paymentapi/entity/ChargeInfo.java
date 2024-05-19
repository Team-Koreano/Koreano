package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.utils.BeanPayTimeFormatUtil.*;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private PaymentDetail paymentDetail;

	@Column(length = 50)
	private String paymentKey;

	@Column(name = "pay_type")
	private String payType;

	@Column(updatable = false)
	private LocalDateTime approveDateTime;

	public static ChargeInfo ofCharge(
		String paymentKey,
		String approveDateTime
	) {
		ChargeInfo chargeInfo = new ChargeInfo();
		chargeInfo.paymentKey = paymentKey;
		chargeInfo.paymentDetail = new PaymentDetail();
		chargeInfo.approveDateTime = stringToDateTime(approveDateTime);
		return chargeInfo;
	}

	public void chargeComplete(

	) {

	}
}
