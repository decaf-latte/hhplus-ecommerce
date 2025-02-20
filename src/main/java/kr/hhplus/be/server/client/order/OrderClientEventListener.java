package kr.hhplus.be.server.client.order;

import kr.hhplus.be.server.client.DataPlatformService;
import kr.hhplus.be.server.domain.outbox.entity.OutboxEvent;
import kr.hhplus.be.server.domain.outbox.repository.OutboxEventRepository;
import kr.hhplus.be.server.event.order.PaidOrderEvent;
import kr.hhplus.be.server.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClientEventListener {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void saveOutbox(PaidOrderEvent event) {
        outboxEventRepository.save(OutboxEvent.of(event));
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendOrderInfo(PaidOrderEvent event) {
        kafkaTemplate.send("order-paid", JsonUtil.convertToJson(event));
        log.info("OrderPaidEvent published to Kafka: {}", event);
    }
}
