package kr.hhplus.be.server.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.order.code.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.order.OrderServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("주문 생성 성공")
    void createOrderSuccess() {
        User user = User.of().name("John Doe").email("john.doe@example.com").build();
        BigDecimal totalPrice = new BigDecimal("100");
        Order order = Order.of().user(user).status(OrderStatus.PENDING).totalPrice(totalPrice).build();

        when(orderRepository.save(any(Order.class))).thenReturn(order);

        Order createdOrder = orderService.createOrder(user, totalPrice);

        assertThat(createdOrder).isNotNull();
        assertThat(createdOrder.getUser()).isEqualTo(user);
        assertThat(createdOrder.getStatus()).isEqualTo(OrderStatus.PENDING);
        assertThat(createdOrder.getTotalPrice()).isEqualTo(totalPrice);
    }
}