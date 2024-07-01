package org.ecommerce.productapi.messaging;

import org.ecommerce.productapi.dto.SellerCreateMessage;
import org.ecommerce.productapi.entity.SellerRep;
import org.ecommerce.productapi.repository.SellerRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class KafkaConsumer {
	private final SellerRepository sellerRepository;

	@KafkaListener(topics = "create-seller-topic", groupId = "consumerGroupId")
	public void updateSeller(final SellerCreateMessage message) {
		sellerRepository.save(new SellerRep(message.getSellerId(), message.getBizName()));
	}
}
