package kr.hhplus.be.server.event.order;

import lombok.Getter;

@Getter
public class PaidOrderEvent {
    private final Long orderId;

    public PaidOrderEvent(Long orderId) {
        this.orderId = orderId;
    }

}
