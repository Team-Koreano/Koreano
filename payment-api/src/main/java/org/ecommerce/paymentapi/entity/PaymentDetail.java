package org.ecommerce.paymentapi.entity;

import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "payment_detail")
public class PaymentDetail {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Payment payment;

	@ManyToOne(
		fetch = FetchType.LAZY,
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
	})
	@JoinColumn(name = "beanpay_detail_user_id", nullable = false)
	private BeanPayDetail userBeanPayDetail;

	@ManyToOne(
		fetch = FetchType.LAZY,
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
		})
	@JoinColumn(name = "beanpay_detail_seller_id", nullable = false)
	private BeanPayDetail sellerBeanPayDetail;

	@Column(nullable = false)
	private Long orderDetailId;

	@Column(nullable = false)
	private Integer totalPrice = 0;

	@Column(nullable = false)
	private Integer deliveryFee = 0;

	@Column(nullable = false)
	private Integer paymentAmount = 0;

	@Column(nullable = false)
	private Integer quantity = 0;

	@Column(nullable = false)
	private String productName;

	@Column
	private String cancelReason;

	@Column
	private String failReason;


	@Column
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus = PAYMENT;

	@Column
	@Enumerated(EnumType.STRING)
	private ProcessStatus processStatus = CANCELLED;

	@OneToMany(
		mappedBy = "paymentDetail",
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
		}
	)
	private List<PaymentStatusHistory> paymentStatusHistories = new LinkedList<>();

	@CreationTimestamp
	@Column(name = "create_datetime", updatable = false)
	private LocalDateTime createDateTime;

	@UpdateTimestamp
	@Column(name = "update_datetime", insertable = false)
	private LocalDateTime updateDateTime;

	@Column(name = "is_deleted")
	private Boolean isDeleted;

	public static PaymentDetail ofPayment(
		final Payment payment,
		final BeanPay sellerBeanPay,
		final Long orderDetailId,
		final Integer totalPrice,
		final Integer deliveryFee,
		final Integer paymentAmount,
		final Integer quantity,
		final String productName
	) {
		final BeanPay userBeanPay = payment.getUserBeanPay();

		final PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.payment = payment;
		paymentDetail.userBeanPayDetail = BeanPayDetail.ofPayment(
			userBeanPay,
			paymentAmount
		);
		paymentDetail.sellerBeanPayDetail = BeanPayDetail.ofReceive(
			sellerBeanPay,
			paymentAmount
		);
		paymentDetail.orderDetailId = orderDetailId;
		paymentDetail.totalPrice = totalPrice;
		paymentDetail.deliveryFee = deliveryFee;
		paymentDetail.paymentAmount = paymentAmount;
		paymentDetail.quantity = quantity;
		paymentDetail.productName = productName;

		paymentDetail.paymentStatusHistories.add(
			PaymentStatusHistory.ofRecord(paymentDetail)
		);
		// 각 결제 디테일 계산
		userBeanPay.payment(totalPrice, sellerBeanPay);

		return paymentDetail;
	}

	/**
	 결제 취소 이벤트 발생 시 롤백
	 1. 실수 이유 저장
	 2. 상태  히스토리 추가
	 3. user, seller 빈페이 롤백
	 * @author 이우진
	 *
	 * @param - String message
	 * @return - void
	 */
	public void cancelPayment(final String message) {
		changeProcessStatus(CANCELLED);
		this.failReason = message;
		this.paymentStatusHistories.add(
			PaymentStatusHistory.ofRecord(this)
		);
		this.userBeanPayDetail.rollbackPayment(
			this.getTotalPrice(),
			this.sellerBeanPayDetail.getBeanPay(),
			failReason
		);
	}

	private void changeProcessStatus(final ProcessStatus status) {
		this.processStatus = status;
	}
}
