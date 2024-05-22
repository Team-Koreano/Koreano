package org.ecommerce.paymentapi.repository;

import org.ecommerce.paymentapi.entity.ChargeInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChargeInfoRepository extends JpaRepository<ChargeInfo, Long> {
}
