package org.ecommerce.orderapi.stock.event.publisher;

import java.util.List;

import org.ecommerce.orderapi.stock.dto.StockOperationMessage;
import org.ecommerce.orderapi.stock.messaging.KafkaProducer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockDecreasedEventKafkaPublisher {

	private final KafkaProducer kafkaProducer;

	public void publish(final List<StockOperationMessage> stockOperationMessages) {
		for (StockOperationMessage stockOperationMessage : stockOperationMessages) {
			kafkaProducer.send(stockOperationMessage);
		}
	}
}
