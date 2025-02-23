package kr.hhplus.be.server.controller.order.application;

import kr.hhplus.be.server.config.KafkaTestConfig;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.controller.exception.CommerceOrderException;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.cart.entity.CartItem;
import kr.hhplus.be.server.domain.cart.repository.CartItemRepository;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.domain.outbox.code.OutboxEventStatus;
import kr.hhplus.be.server.domain.outbox.entity.OutboxEvent;
import kr.hhplus.be.server.domain.outbox.repository.OutboxEventRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.service.order.vo.OrderVO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, topics = "order-paid")
@Import(KafkaTestConfig.class)
class OrderApplicationServiceTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql");

    private static MockWebServer mockWebServer;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderApplicationServiceImpl orderApplicationServiceImpl;

    @Autowired
    private OutboxEventRepository outboxEventRepository;

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(); // MockWebServer 시작

    }

    @BeforeEach
    void clearOutboxTable() {
        outboxEventRepository.deleteAll();
    }

    @AfterAll
    static void tearDownMockWebServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown(); // MockWebServer 중지
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("dataPlatformClient.url",
                () -> mockWebServer.url("/api").toString());
    }

    @BeforeEach
    void setUpMockResponses() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("true"));

    }

    @Test
    @DisplayName("주문 결제 성공")
    void payOrder_Success() {
        // Given: 데이터베이스에서 필요한 데이터 조회
        User user = userRepository.findById(1L).orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));
        CartItem cartItem = cartItemRepository.findById(1L).orElseThrow(() -> new CommerceOrderException(ErrorCode.CART_ITEM_COUNT_MISMATCH));

        // When: 주문 결제
        OrderVO result = orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), null);

        // Then: 결제가 정상적으로 이루어졌는지 검증
        assertNotNull(result);
        assertEquals(user.getId(), result.getUser().getId());

    }


    @Test
    @DisplayName("주문 결제 실패 - 잔액 부족")
    void payOrder_InsufficientBalance() {
        // Given: 잔액 부족한 사용자
        User user = userRepository.findById(2L).orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));
        CartItem cartItem = cartItemRepository.findById(2L).orElseThrow(() -> new CommerceOrderException(ErrorCode.CART_ITEM_COUNT_MISMATCH));

        // When / Then: 잔액 부족 예외 확인
        CommerceUserException exception = assertThrows(CommerceUserException.class, () -> {
            orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), null);
        });
        assertEquals("잔액이 부족합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 결제 실패 - 쿠폰 만료")
    void payOrder_CouponExpired() {
        // Given: 만료된 쿠폰
        User user = userRepository.findById(2L).orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));
        CartItem cartItem = cartItemRepository.findById(2L).orElseThrow(() -> new CommerceOrderException(ErrorCode.CART_ITEM_COUNT_MISMATCH));

        // When / Then: 만료된 쿠폰 예외 확인
        CommerceCouponException exception = assertThrows(CommerceCouponException.class, () -> {
            orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), 2L);
        });
        assertEquals("사용할 수 없는 쿠폰입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("Outbox 이벤트 저장 확인")
    public void testPayOrder_SavesEventToOutbox(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        // Given
        String brokerAddress = embeddedKafkaBroker.getBrokersAsString();

        CartItem cartItem = cartItemRepository.findById(3L).orElseThrow(() -> new CommerceOrderException(ErrorCode.CART_ITEM_COUNT_MISMATCH));

        // When: 주문 결제
        orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), null);

        // Then
        List<OutboxEvent> events = outboxEventRepository.findAll();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getStatus()).isEqualTo(OutboxEventStatus.PENDING);

        System.out.println("Kafka Broker Address: " + brokerAddress);
    }

    @Test
    @DisplayName("Kafka로 발행된 후 Outbox 상태가 PUBLISHED로 변경되는지 확인")
    public void testOutboxEventProcessedByKafka(@Autowired EmbeddedKafkaBroker embeddedKafkaBroker) {
        // Given: Outbox 이벤트가 PENDING 상태로 저장됨
        CartItem cartItem = cartItemRepository.findById(4L)
                .orElseThrow(() -> new CommerceOrderException(ErrorCode.CART_ITEM_COUNT_MISMATCH));

        orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), null);

        // When: Kafka 메시지가 전송된 후 상태 변경 확인
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<OutboxEvent> events = outboxEventRepository.findAll();
            assertThat(events).hasSize(1);
            assertThat(events.get(0).getStatus()).isEqualTo(OutboxEventStatus.PUBLISHED);
        });

        System.out.println("Kafka Broker Address: " + embeddedKafkaBroker.getBrokersAsString());
    }
}
