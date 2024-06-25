package org.ecommerce.paymentapi.entity;

import static java.lang.Boolean.*;
import static org.ecommerce.paymentapi.entity.enumerate.PaymentStatus.*;
import static org.ecommerce.paymentapi.entity.enumerate.ProcessStatus.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.ecommerce.common.error.CustomException;
import org.ecommerce.paymentapi.dto.response.TossPaymentResponse;
import org.ecommerce.paymentapi.entity.enumerate.PaymentStatus;
import org.ecommerce.paymentapi.entity.enumerate.ProcessStatus;
import org.ecommerce.paymentapi.exception.PaymentDetailErrorCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "payment_detail",
	indexes = {
		@Index(name = "idx_order_item_id", columnList = "orderItemId"),
		@Index(name = "idx_create_datetime", columnList = "createDateTime"),
	}
)
public class PaymentDetail {

	@Id
	@GeneratedValue(generator = "uuid2")
	@GenericGenerator(name = "uuid2", strategy = "uuid2")
	@Column(columnDefinition = "BINARY(16)")
	private UUID id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "payment_id")
	private Payment payment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beanpay_user_id", nullable = false)
	private UserBeanPay userBeanPay;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "beanpay_seller_id")
	private SellerBeanPay sellerBeanPay;

	@Column(unique = true)
	private Long orderItemId;

	@Column
	private Integer price = 0;

	@Column
	private Integer quantity = 0;

	@Column
	private Integer deliveryFee = 0;

	@Column
	private Integer totalAmount = 0;

	@Column(nullable = false)
	private Integer paymentAmount = 0;



	@Column
	private String paymentName;

	@Column
	private String cancelReason;

	@Column
	private String failReason;

	@OneToOne(
		fetch = FetchType.LAZY,
		cascade = {
			CascadeType.PERSIST,
			CascadeType.MERGE
		}
	)
	@JoinColumn(name = "charge_info")
	private ChargeInfo chargeInfo;

	@Column
	@Enumerated(EnumType.STRING)
	private PaymentStatus paymentStatus;

	@Column
	@Enumerated(EnumType.STRING)
	private ProcessStatus processStatus = PENDING;

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

	@Column(name = "is_visible")
	private Boolean isVisible = TRUE;

	protected static PaymentDetail ofBeforeCharge(UserBeanPay userBeanPay, Integer chargeAmount){
		PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.userBeanPay = userBeanPay;
		paymentDetail.paymentAmount = chargeAmount;
		paymentDetail.paymentStatus = DEPOSIT;
		return paymentDetail;
	}

	protected static PaymentDetail ofPayment(
		final Payment payment,
		final SellerBeanPay sellerBeanPay,
		final Long orderDetailId,
		final Integer deliveryFee,
		final Integer quantity,
		final Integer price,
		final String productName
	) {
		final UserBeanPay userBeanPay = payment.getUserBeanPay();

		final PaymentDetail paymentDetail = new PaymentDetail();
		paymentDetail.payment = payment;
		paymentDetail.userBeanPay = userBeanPay;
		paymentDetail.sellerBeanPay = sellerBeanPay;
		paymentDetail.orderItemId = orderDetailId;
		paymentDetail.price = price;
		paymentDetail.quantity = quantity;
		paymentDetail.deliveryFee = deliveryFee;
		paymentDetail.totalAmount = quantity * price;
		paymentDetail.paymentAmount = quantity * price + deliveryFee;
		paymentDetail.paymentName = productName;
		paymentDetail.paymentStatus = PAYMENT;

		paymentDetail.paymentStatusHistories.add(
			PaymentStatusHistory.ofRecord(paymentDetail)
		);
		// 각 결제 디테일 계산
		userBeanPay.payment(paymentDetail.getPaymentAmount(), sellerBeanPay);
		payment.increaseTotalAmount(paymentDetail.getPaymentAmount());
		paymentDetail.changeProcessStatus(COMPLETED);

		return paymentDetail;
	}


	public void calculatePaymentAmount() {

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
	public PaymentDetail cancelPaymentDetail(final String message) {
		changeProcessStatus(CANCELLED);
		changePaymentStatus(REFUND);
		this.cancelReason = message;
		this.paymentStatusHistories.add(
			PaymentStatusHistory.ofRecord(this)
		);
		this.userBeanPay.cancelPayment(getPaymentAmount(), this.sellerBeanPay);
		this.payment.decreaseTotalAmount(this.getPaymentAmount());
		return this;
	}

	public boolean validCharge(UUID orderId, Integer amount) {
		beginValidProcess();
		return this.getId().equals(orderId) &&
			this.getPaymentAmount().equals(amount);
	}

	public void chargeComplete(TossPaymentResponse response) {
		if(this.chargeInfo != null)
			throw new CustomException(PaymentDetailErrorCode.DUPLICATE_API_CALL);

		this.chargeInfo = ChargeInfo.ofCharge(
			response.paymentKey(),
			response.approveDateTime());
		this.paymentName = response.orderName();
		changeProcessStatus(COMPLETED);
		this.userBeanPay.chargeBeanPayDetail(response.totalAmount());
	}

	public void chargeFail(String message) {
		this.failReason = message;
		changeProcessStatus(ProcessStatus.FAILED);
	}

	public void chargeCancel(String message) {
		this.cancelReason = message;
		changeProcessStatus(ProcessStatus.CANCELLED);
	}

	private void beginValidProcess() {
		this.processStatus = ProcessStatus.IN_PROGRESS;
	}


	private void changeProcessStatus(final ProcessStatus status) {
		this.processStatus = status;
	}
	private void changePaymentStatus(PaymentStatus status) {
		this.paymentStatus = status;
	}

}
