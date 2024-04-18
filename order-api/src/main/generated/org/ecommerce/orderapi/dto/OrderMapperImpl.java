package org.ecommerce.orderapi.dto;

import java.time.LocalDateTime;
import javax.annotation.processing.Generated;
import org.ecommerce.orderapi.entity.Order;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-18T20:13:20+0900",
    comments = "version: 1.5.3.Final, compiler: javac, environment: Java 17.0.10 (Amazon.com Inc.)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderDto toDto(Order order) {
        if ( order == null ) {
            return null;
        }

        Long id = null;
        Integer userId = null;
        String receiveName = null;
        String phoneNumber = null;
        String address1 = null;
        String address2 = null;
        String deliveryComment = null;
        Integer beanpay = null;
        LocalDateTime paymentDatetime = null;
        LocalDateTime orderDatetime = null;

        id = order.getId();
        userId = order.getUserId();
        receiveName = order.getReceiveName();
        phoneNumber = order.getPhoneNumber();
        address1 = order.getAddress1();
        address2 = order.getAddress2();
        deliveryComment = order.getDeliveryComment();
        beanpay = order.getBeanpay();
        paymentDatetime = order.getPaymentDatetime();
        orderDatetime = order.getOrderDatetime();

        OrderDto orderDto = new OrderDto( id, userId, receiveName, phoneNumber, address1, address2, deliveryComment, beanpay, paymentDatetime, orderDatetime );

        return orderDto;
    }
}
