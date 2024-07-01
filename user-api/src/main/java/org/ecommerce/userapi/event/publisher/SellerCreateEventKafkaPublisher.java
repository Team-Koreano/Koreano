package org.ecommerce.userapi.event.publisher;

import org.ecommerce.userapi.dto.SellerCreateMessage;
import org.ecommerce.userapi.messaging.KafkaProducer;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SellerCreateEventKafkaPublisher {

	private final KafkaProducer kafkaProducer;

	public void publish(final SellerCreateMessage sellerCreateMessage) {
		kafkaProducer.send(sellerCreateMessage);
	}

}
