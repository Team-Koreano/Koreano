INSERT INTO user_beanpay (id, user_id, amount, create_date_time, delete_date_time)
VALUES (999, 999, 100000, now(), null);

INSERT INTO seller_beanpay(id, seller_id, amount, create_date_time, delete_date_time)
VALUES (1000, 1000, 0, now(), null);

INSERT INTO charge_info(id, payment_key, pay_type, approve_date_time)
VALUES
    (1, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (2, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (3, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (4, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (5, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (6, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (7, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (8, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (9, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now()),
    (10, '31ccee8266c54f6cb50b6efa8b62e8a0', '카드', now());

INSERT INTO payment_detail(id, payment_id, beanpay_user_id, beanpay_seller_id, order_item_id, price, quantity, delivery_fee, total_amount, payment_amount, payment_name, cancel_reason, fail_reason, charge_info, payment_status, process_status, create_datetime, update_datetime, is_visible)
VALUES
    ('31ccee8266c54f61', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 1, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f62', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 2, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f63', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 3, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f64', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 4, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f65', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 5, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f66', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 6, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f67', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 7, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f68', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 8, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f69', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 9, 'DEPOSIT', 'COMPLETED', now(), now(), true),
    ('31ccee8266c54f60', null, 999, null, null, 0, 0, 0, 0, 10000, '1만원 충전', null, null, 10, 'DEPOSIT', 'COMPLETED', now(), now(), true);