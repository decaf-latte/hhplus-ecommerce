package kr.hhplus.be.server.service.order.vo;

import java.math.BigDecimal;
import java.util.List;
import kr.hhplus.be.server.domain.order.code.OrderStatus;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.service.payment.vo.PaymentVO;
import kr.hhplus.be.server.service.user.vo.UserVO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor
public class OrderVO {

    private Long id;
    private UserVO user;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderItemVO> orderItems;
    private PaymentVO payment;

    @Builder
    public OrderVO(Long id, UserVO user, BigDecimal totalPrice, OrderStatus status,
                   List<OrderItemVO> orderItems, PaymentVO payment) {
        this.id = id;
        this.user = user;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderItems = orderItems;
        this.payment = payment;
    }

    public static OrderVO from(Order order) {

        if (ObjectUtils.isEmpty(order)) {
            return null;
        }

        List<OrderItemVO> orderItemVOS = order.getOrderItems().stream().map(OrderItemVO::from).toList();

        return OrderVO.builder()
                .id(order.getId())
                .user(UserVO.from(order.getUser()))
                .totalPrice(order.getTotalPrice())
                .status(order.getStatus())
                .orderItems(orderItemVOS)
                .payment(PaymentVO.from(order.getPayment()))
                .build();
    }
}
