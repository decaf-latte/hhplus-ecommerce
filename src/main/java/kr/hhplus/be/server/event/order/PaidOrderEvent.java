package kr.hhplus.be.server.event.order;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class PaidOrderEvent {
    private final Long orderId;

    public static PaidOrderEvent of(Long orderId) {
        return new PaidOrderEvent(orderId);
    }

}
