package kr.hhplus.be.server.controller.kafka;

import kr.hhplus.be.server.config.global.kafka.KafkaProducer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KafkaController {

    private final KafkaProducer kafkaProducer;

    public KafkaController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/kafka/send")
    public String sendMessage(@RequestParam String message) {
        kafkaProducer.sendMessage("test-topic", message);
        return "Message sent: " + message;
    }
}
