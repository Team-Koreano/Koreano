package org.ecommerce.orderapi.handler;

import java.time.LocalDateTime;
import java.util.List;

import org.ecommerce.orderapi.dto.StockDto;
import org.ecommerce.orderapi.event.StockDecreasedEvent;
import org.ecommerce.orderapi.event.StockIncreasedEvent;
import org.ecommerce.orderapi.service.StockHelper;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventHandler {

	private final StockHelper stockHelper;

	public List<StockDto> decreaseStocks(final Long orderId) {
		List<StockDto> stockDtos = stockHelper.decreaseStocks(orderId);

		// TODO stockDecreasedEvents 발행
		List<StockDecreasedEvent> stockDecreasedEvents = stockDtos.stream()
				.map(stockDto -> new StockDecreasedEvent(
						stockDto.getId(),
						LocalDateTime.now())
				).toList();
		return stockDtos;
	}

	public StockDto increaseStock(final Long orderId, final Long orderItemId) {
		StockDto stockDto = stockHelper.increaseStock(orderId, orderItemId);
		// TODO stockIncreasedEvent 발행
		new StockIncreasedEvent(stockDto.getId(), LocalDateTime.now());
		return stockDto;
	}
}
