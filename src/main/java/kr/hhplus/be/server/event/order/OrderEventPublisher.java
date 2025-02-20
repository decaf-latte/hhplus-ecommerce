package kr.hhplus.be.server.event.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public void publishPaidOrderEvent(PaidOrderEvent paidOrderEvent) {
        applicationEventPublisher.publishEvent(paidOrderEvent);
    }
}
