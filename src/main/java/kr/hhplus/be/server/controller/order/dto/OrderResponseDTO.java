package kr.hhplus.be.server.controller.order.dto;

import kr.hhplus.be.server.domain.order.code.OrderStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class OrderResponseDTO {
    private long orderId;
    private long userId;
    private int price;
    private int discount;
    private int finalPrice;
    private int remainingBalance;
    private OrderStatus status;
    private LocalDateTime orderDate;


    @Builder
    public OrderResponseDTO(long orderId, long userId, int price, int discount, int finalPrice, int remainingBalance, OrderStatus status, LocalDateTime orderDate) {
        this.orderId = orderId;
        this.userId = userId;
        this.price = price;
        this.discount = discount;
        this.finalPrice = finalPrice;
        this.remainingBalance = remainingBalance;
        this.status = status;
        this.orderDate = orderDate;
    }
}
