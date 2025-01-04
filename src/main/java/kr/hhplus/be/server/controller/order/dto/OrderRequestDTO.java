package kr.hhplus.be.server.controller.order.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OrderRequestDTO {
    private long userId;
    private List<OrderItemDTO> orderItems;

    @Builder
    public OrderRequestDTO(long userId, List<OrderItemDTO> orderItems) {
        this.userId = userId;
        this.orderItems = orderItems;
    }


    @Getter
    @Setter
    @NoArgsConstructor
    private static class OrderItemDTO {
        private long productId;
        private int quantity;
        private int price;


        @Builder
        public OrderItemDTO(long productId, int quantity, int price) {
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }
    }


}
