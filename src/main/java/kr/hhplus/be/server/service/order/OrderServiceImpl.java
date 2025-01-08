package kr.hhplus.be.server.service.order;

import kr.hhplus.be.server.domain.order.code.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Override
    public Order createOrder(User user, BigDecimal totalPrice) {
        Order order = Order.of()
                .user(user)
                .status(OrderStatus.PENDING)
                .totalPrice(totalPrice)
                .build();

        return orderRepository.save(order);
    }
}
