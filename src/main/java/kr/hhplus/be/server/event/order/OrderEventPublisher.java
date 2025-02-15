package kr.hhplus.be.server.event.order;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public OrderEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publishPaidOrderEvent(PaidOrderEvent paidOrderEvent) {
        applicationEventPublisher.publishEvent(paidOrderEvent);
    }
}
