package kr.hhplus.be.server.service.payment;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.entity.Payment;

public interface PaymentService {

    Payment save(Order order, BigDecimal amount);
}
