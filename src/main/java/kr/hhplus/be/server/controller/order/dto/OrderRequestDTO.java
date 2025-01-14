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

    private List<Long> cartItemIds;
    private Long userCouponId;

    @Builder
    public OrderRequestDTO(List<Long> cartItemIds, Long userCouponId) {
        this.cartItemIds = cartItemIds;
        this.userCouponId = userCouponId;
    }
}
