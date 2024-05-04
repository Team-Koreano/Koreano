package org.ecommerce.orderapi.entity;

import org.ecommerce.orderapi.entity.enumerated.StockOperationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock_history",
		indexes = {@Index(name = "idx_orderDetailId", columnList = "orderDetailId")})
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class StockHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Stock stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private OrderDetail orderDetail;

	@Column
	@Enumerated(EnumType.STRING)
	private StockOperationType operationType;

	public static StockHistory ofRecord(
			final Stock stock,
			final OrderDetail orderDetail,
			final StockOperationType operationType
	) {
		final StockHistory stockHistory = new StockHistory();
		stockHistory.stock = stock;
		stockHistory.orderDetail = orderDetail;
		stockHistory.operationType = operationType;
		return stockHistory;
	}
}
