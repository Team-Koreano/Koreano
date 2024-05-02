package org.ecommerce.orderapi.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "orders")
@Getter
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Integer userId;

	@Column(nullable = false)
	private String receiveName;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String address1;

	@Column(nullable = false)
	private String address2;

	@Column
	private String deliveryComment;

	@Column
	private Integer totalPaymentAmount;

	@Column
	private LocalDateTime paymentDatetime;

	@CreationTimestamp
	@Column
	private LocalDateTime orderDatetime;

	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderDetail> orderDetails = new ArrayList<>();

	public static Order ofPlace(
			final Integer userId,
			final String receiveName,
			final String phoneNumber,
			final String address1,
			final String address2,
			final String deliveryComment,
			final List<Product> products,
			final Map<Integer, Integer> productIdToQuantityMap
	) {
		final Order order = new Order();
		// TODO : 배송비 우선 무료로 고정, 추후 seller에서 정책 설정
		Integer DELIVERY_FEE = 0;
		order.userId = userId;
		order.receiveName = receiveName;
		order.phoneNumber = phoneNumber;
		order.address1 = address1;
		order.address2 = address2;
		order.deliveryComment = deliveryComment;
		order.orderDetails = products.stream()
				.map(product -> OrderDetail.ofPlace(
						order,
						product.getId(),
						product.getPrice(),
						productIdToQuantityMap.get(product.getId()),
						DELIVERY_FEE,
						product.getSeller()
				)).toList();
		order.totalPaymentAmount = order.orderDetails.stream()
				.mapToInt(OrderDetail::getPaymentAmount)
				.sum();
		return order;
	}
}
