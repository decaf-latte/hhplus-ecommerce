package kr.hhplus.be.server.config.global.kafka;

import kr.hhplus.be.server.event.order.PaidOrderEvent;
import kr.hhplus.be.server.util.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "test-topic", groupId = "spring-group")
    public void listen(ConsumerRecord<String, String> record) {
        log.info("Received message: " + record.value() + " from topic " + record.topic());
    }

    @KafkaListener(topics = "paid-orders", groupId = "order-group")
    public void listenPaidOrderEvent(String message) {
        PaidOrderEvent event = JsonUtil.convertFromJson(message, PaidOrderEvent.class);
        log.info("Received PaidOrderEvent: {}", event);
    }
}

