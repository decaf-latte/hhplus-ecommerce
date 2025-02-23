package kr.hhplus.be.server.scheduler.order;

import kr.hhplus.be.server.domain.outbox.code.OutboxEventStatus;
import kr.hhplus.be.server.domain.outbox.entity.OutboxEvent;
import kr.hhplus.be.server.domain.outbox.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderScheduler {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 300000) // 5분마다 실행
    @Transactional
    public void rePublishFailedEvents() {
        List<OutboxEvent> failedEvents = outboxEventRepository.findByStatus(OutboxEventStatus.PENDING);

        for (OutboxEvent event : failedEvents) {
            if (event.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(5))) {
                try {
                    kafkaTemplate.send("order-paid", event.getPayload());
                    event.markAsProcessed(); // 성공 시 상태 변경
                    log.info("Retried and sent event: {}", event.getPayload());
                } catch (Exception e) {
                    log.error("Retry failed: {}", event.getPayload(), e);
                }
            }
        }
        outboxEventRepository.saveAll(failedEvents);
    }
}