package kr.hhplus.be.server.service.order.vo;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor
public class OrderItemVO {

    private Long id;
    private Long orderId;
    private ProductVO product;
    private int quantity;
    private BigDecimal price;

    @Builder
    public OrderItemVO(Long id, Long orderId, ProductVO product, int quantity, BigDecimal price) {
        this.id = id;
        this.orderId = orderId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
    }

    public static OrderItemVO from(OrderItem orderItem) {

        if (ObjectUtils.isEmpty(orderItem)) {
            return null;
        }

        return OrderItemVO.builder()
                .id(orderItem.getId())
                .orderId(orderItem.getOrder().getId())
                .product(ProductVO.from(orderItem.getProduct()))
                .quantity(orderItem.getQuantity())
                .price(orderItem.getPrice())
                .build();
    }
}
