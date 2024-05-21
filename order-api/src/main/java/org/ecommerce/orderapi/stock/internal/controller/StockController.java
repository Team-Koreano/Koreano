package org.ecommerce.orderapi.stock.internal.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/internal/stocks/v1")
public class StockController {
	// TODO : seller 재고 증/감
}
