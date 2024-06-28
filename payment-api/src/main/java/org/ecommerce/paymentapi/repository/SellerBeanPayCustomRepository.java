package org.ecommerce.paymentapi.repository;

import java.util.List;

import org.ecommerce.paymentapi.entity.SellerBeanPay;

public interface SellerBeanPayCustomRepository {
	SellerBeanPay findSellerBeanPayBySellerId(Integer userId);
	List<SellerBeanPay> findSellerBeanPayBySellerIds(List<Integer> userIds);
}
