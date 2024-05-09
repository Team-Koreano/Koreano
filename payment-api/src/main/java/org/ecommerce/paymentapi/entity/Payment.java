package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.paymentapi.dto.PaymentDetailDto.Request.PaymentDetailPrice;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.context.annotation.Bean;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(
	name = "payment",
	indexes = {
		@Index(name = "idx_orderId", columnList = "orderId"),
	}
)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_id", nullable = false, unique = true)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beanpay_detail_user_id", nullable = false)
	private BeanPay userBeanPay;

	@ColumnDefault("0")
	@Column(name = "total_amount", nullable = false)
	private Integer totalAmount;

	@Column(name = "order_name", nullable = false)
	private String orderName;

	@Enumerated
	private ProcessStatus status = ProcessStatus.PENDING;

	@OneToMany(
		mappedBy = "payment",
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
		}
	)
	private List<PaymentDetail> paymentDetails = new ArrayList<>();

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;

	@UpdateTimestamp
	@Column(name = "update_datetime", insertable = false)
	private LocalDateTime updateDateTime;

	@Column(name = "is_deleted")
	private Boolean isVisible = Boolean.TRUE;

	public static Payment ofPayment(
		BeanPay userBeanPay,
		List<BeanPay> selllerBeanPays,
		Long orderId,
		Integer totalAmount,
		String orderName,
		List<PaymentDetailPrice> paymentDetails
	) {
		Payment payment = new Payment();
		payment.orderId = orderId;
		payment.userBeanPay = userBeanPay;
		payment.totalAmount = totalAmount;
		payment.orderName = orderName;
		payment.changeProcessStatus(COMPLETED);
		createPaymentDetails(selllerBeanPays, paymentDetails, payment);

		return payment;
	}

	private static void createPaymentDetails(
		List<BeanPay> sellerBeanPays,
		List<PaymentDetailPrice> paymentDetailPrices,
		Payment payment
	) {
		for(int i = 0; i < sellerBeanPays.size(); i++) {
			PaymentDetailPrice paymentDetailPrice = paymentDetailPrices.get(i);
			BeanPay sellerBeanPay = sellerBeanPays.get(i);

			payment.paymentDetails.add(
				PaymentDetail.ofPayment(
					payment,
					sellerBeanPay,
					paymentDetailPrice.orderDetailId(),
					paymentDetailPrice.totalPrice(),
					paymentDetailPrice.deliveryFee(),
					paymentDetailPrice.paymentAmount(),
					paymentDetailPrice.quantity(),
					paymentDetailPrice.productName()
				)
			);
		}
	}

	public Payment rollbackPayment(String message) {
		changeProcessStatus(CANCELLED);
		this.paymentDetails.forEach( paymentDetail ->
			paymentDetail.rollbackPayment(message)
		);
		return this;
	}

	private void changeProcessStatus(ProcessStatus status) {
		this.status = status;
	}
}
