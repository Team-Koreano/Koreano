package org.ecommerce.orderapi.entity;

import static org.ecommerce.orderapi.entity.type.StockOperationType.*;

import org.ecommerce.orderapi.entity.type.StockOperationType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table(name = "stock_history",
		indexes = {@Index(name = "idx_orderDetailId", columnList = "orderDetailId")})
@Getter
public class StockHistory {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private Long orderDetailId;

	@Column(nullable = false)
	private Integer productId;

	@Column(nullable = false)
	private Integer quantity;

	@Column
	@Enumerated(EnumType.STRING)
	private StockOperationType operationType = INCREASE;

	public static StockHistory ofRecord(
			final Long orderDetailId,
			final Integer productId,
			final Integer quantity
	) {
		final StockHistory stockHistory = new StockHistory();
		stockHistory.orderDetailId = orderDetailId;
		stockHistory.productId = productId;
		stockHistory.quantity = quantity;
		return stockHistory;
	}
}
