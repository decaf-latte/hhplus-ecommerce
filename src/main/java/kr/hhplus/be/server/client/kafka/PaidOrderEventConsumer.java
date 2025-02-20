package kr.hhplus.be.server.client.kafka;

import kr.hhplus.be.server.domain.outbox.repository.OutboxEventRepository;
import kr.hhplus.be.server.event.order.PaidOrderEvent;
import kr.hhplus.be.server.util.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaidOrderEventConsumer {

    private final OutboxEventRepository outboxEventRepository;

    @KafkaListener(topics = "order-paid", groupId = "order-group")
    public void consume(String message) {
        log.info("Received Kafka message: {}", message);

        try {
            PaidOrderEvent event = JsonUtil.convertFromJson(message, PaidOrderEvent.class);
            log.info("Successfully converted to PaidOrderEvent: {}", event);

            // Outbox 상태를 PUBLISHED로 변경
            outboxEventRepository.findByAggregateId(event.getOrderId())
                    .ifPresent(outboxEvent -> {
                        outboxEvent.markAsProcessed();
                        outboxEventRepository.save(outboxEvent);
                        log.info("Outbox event processed and marked as PUBLISHED: {}", outboxEvent);
                    });

        } catch (Exception e) {
            log.error("JSON Parsing Error: {}", message, e);
            throw new RuntimeException("Failed to convert JSON to object", e);
        }
    }


}
