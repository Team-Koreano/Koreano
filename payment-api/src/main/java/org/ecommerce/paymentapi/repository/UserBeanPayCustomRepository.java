package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.UserBeanPay;

public interface UserBeanPayCustomRepository {
	UserBeanPay findUserBeanPayByUserId(Integer userId);
	UserBeanPay findUserBeanPayByUserIdUseBetaLock(Integer userId);
}
