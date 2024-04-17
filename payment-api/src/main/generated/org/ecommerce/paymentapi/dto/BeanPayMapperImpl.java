package org.ecommerce.paymentapi.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import javax.annotation.processing.Generated;
import org.ecommerce.paymentapi.entity.BeanPay;
import org.ecommerce.paymentapi.entity.type.BeanPayStatus;
import org.ecommerce.paymentapi.entity.type.ProcessStatus;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-16T19:21:05+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.8 (Amazon.com Inc.)"
)
@Component
public class BeanPayMapperImpl implements BeanPayMapper {

    @Override
    public BeanPayDto toDto(BeanPay beanPay) {
        if ( beanPay == null ) {
            return null;
        }

        UUID id = null;
        String paymentKey = null;
        Integer userId = null;
        Integer amount = null;
        String payType = null;
        String cancelOrFailReason = null;
        BeanPayStatus beanPayStatus = null;
        ProcessStatus processStatus = null;
        LocalDateTime createDateTime = null;
        LocalDateTime approveDateTime = null;

        id = beanPay.getId();
        paymentKey = beanPay.getPaymentKey();
        userId = beanPay.getUserId();
        amount = beanPay.getAmount();
        payType = beanPay.getPayType();
        cancelOrFailReason = beanPay.getCancelOrFailReason();
        beanPayStatus = beanPay.getBeanPayStatus();
        processStatus = beanPay.getProcessStatus();
        createDateTime = beanPay.getCreateDateTime();
        approveDateTime = beanPay.getApproveDateTime();

        BeanPayDto beanPayDto = new BeanPayDto( id, paymentKey, userId, amount, payType, cancelOrFailReason, beanPayStatus, processStatus, createDateTime, approveDateTime );

        return beanPayDto;
    }
}
