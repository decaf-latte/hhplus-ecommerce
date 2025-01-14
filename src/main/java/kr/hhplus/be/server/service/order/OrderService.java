package kr.hhplus.be.server.service.order;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.user.entity.User;

import java.math.BigDecimal;

public interface OrderService {

    Order createOrder(User user, BigDecimal totalPrice);
}
