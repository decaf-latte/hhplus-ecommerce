package kr.hhplus.be.server.controller.kafka;

import kr.hhplus.be.server.ServerApplication;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ContextConfiguration(classes = ServerApplication.class)
@EmbeddedKafka(partitions = 1, topics = { "test-topic" })  // 테스트용 Kafka 브로커 실행
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS) // 테스트 후 컨텍스트 초기화
public class KafkaIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private static Consumer<String, String> consumer;

    @BeforeAll
    public static void setup(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafkaBroker);
        ConsumerFactory<String, String> consumerFactory = new DefaultKafkaConsumerFactory<>(
                consumerProps, new StringDeserializer(), new StringDeserializer());

        consumer = consumerFactory.createConsumer();
        embeddedKafkaBroker.consumeFromAnEmbeddedTopic(consumer, "test-topic");
    }

    @Test
    public void testKafkaProducerAndConsumer() {
        String testMessage = "Integration Test Message";

        //Kafka에 메시지 전송
        kafkaTemplate.send("test-topic", testMessage);

        //Kafka Consumer가 메시지를 수신할 때까지 기다림
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            ConsumerRecord<String, String> received = KafkaTestUtils.getSingleRecord(consumer, "test-topic");
            assertThat(received.value()).isEqualTo(testMessage);
        });
    }

    @AfterAll
    public static void cleanup() {
        if (consumer != null) {
            consumer.close();
        }
    }
}
