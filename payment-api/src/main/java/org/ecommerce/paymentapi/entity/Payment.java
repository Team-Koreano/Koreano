package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;
import static org.ecommerce.paymentapi.exception.PaymentErrorCode.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.dto.request.PaymentDetailPriceRequest;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.exception.PaymentDetailErrorCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.util.Pair;

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
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(
	name = "payment",
	indexes = {
		@Index(name = "idx_order_id", columnList = "orderId"),
	}
)
public class Payment {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "order_id", nullable = false, unique = true)
	private Long orderId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beanpay_user_id", nullable = false)
	private BeanPay userBeanPay;

	@Column(name = "total_amount", nullable = false)
	private Integer totalPaymentAmount = 0;

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

	@Column(name = "is_visible")
	private Boolean isVisible = Boolean.TRUE;

	public static Payment ofPayment(
		final BeanPay userBeanPay,
		final Long orderId,
		final String orderName,
		final List<Pair<BeanPay, PaymentDetailPriceRequest>> beanPayPaymentDetailPriceMap
	) {
		Payment payment = new Payment();
		payment.orderId = orderId;
		payment.userBeanPay = userBeanPay;
		payment.orderName = orderName;
		payment.changeProcessStatus(COMPLETED);

		beanPayPaymentDetailPriceMap.forEach((beanPayPaymentDetailPrice) -> {

			BeanPay sellerBeanPay = beanPayPaymentDetailPrice.getFirst();
			PaymentDetailPriceRequest paymentDetailPrice = beanPayPaymentDetailPrice.getSecond();
			//결제 디테일 생성
			payment.paymentDetails.add(
				PaymentDetail.ofPayment(
					payment,
					sellerBeanPay,
					paymentDetailPrice.orderItemId(),
					paymentDetailPrice.deliveryFee(),
					paymentDetailPrice.quantity(),
					paymentDetailPrice.price(),
					paymentDetailPrice.productName()
				)
			);
		});
		return payment;
	}

	public Payment cancelPayment(String message) {
		changeProcessStatus(CANCELLED);
		this.paymentDetails.forEach( paymentDetail ->
			paymentDetail.cancelPaymentDetail(message)
		);
		return this;
	}

	private void changeProcessStatus(ProcessStatus status) {
		this.status = status;
	}

	public PaymentDetail cancelPaymentDetail(Long orderItemId, String message) {
		PaymentDetail cancelPaymentDetail = this.paymentDetails.stream()
			.filter(paymentDetail ->
				paymentDetail.getOrderItemId().equals(orderItemId))
			.findFirst()
			.orElseThrow(() -> new CustomException(PaymentDetailErrorCode.NOT_FOUND_ID))
			.cancelPaymentDetail(message);
		decreaseTotalAmount(cancelPaymentDetail.getPaymentAmount());
		return cancelPaymentDetail;
	}

	private void decreaseTotalAmount(Integer amount) {
		if(this.totalPaymentAmount - amount < 0)
			throw new CustomException(INVALID_AMOUNT);
		this.totalPaymentAmount -= amount;
	}

	protected void increaseTotalAmount(Integer amount) {
		this.totalPaymentAmount += amount;
	}
}
