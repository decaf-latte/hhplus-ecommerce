package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.outbox.repository.OutboxEventRepository;
import kr.hhplus.be.server.scheduler.order.OrderScheduler;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@TestConfiguration
public class OutboxTestConfig {

    @Bean
    public OrderScheduler orderScheduler(OutboxEventRepository repository, KafkaTemplate<String, String> kafkaTemplate) {
        return new OrderScheduler(repository, kafkaTemplate);
    }
}

