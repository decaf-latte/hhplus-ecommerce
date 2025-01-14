package kr.hhplus.be.server.controller.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import kr.hhplus.be.server.client.DataPlatformClient;
import kr.hhplus.be.server.domain.cart.entity.CartItem;
import kr.hhplus.be.server.domain.cart.repository.CartItemRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.service.order.vo.OrderVO;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@Transactional
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

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start(); // MockWebServer 시작

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
        User user = userRepository.findById(1L).orElseThrow(() -> new IllegalStateException("User not found"));
        CartItem cartItem = cartItemRepository.findById(1L).orElseThrow(() -> new IllegalStateException("CartItem not found"));

        // When: 주문 결제
        OrderVO result = orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), null);

        // Then: 결제가 정상적으로 이루어졌는지 검증
        assertNotNull(result);
        assertEquals(user.getId(), result.getUser().getId());

        // MockWebServer 요청 검증 (요청 횟수와 상세 검증)
        assertEquals(1, mockWebServer.getRequestCount());
    }


    @Test
    @DisplayName("주문 결제 실패 - 잔액 부족")
    void payOrder_InsufficientBalance() {
        // Given: 잔액 부족한 사용자
        User user = userRepository.findById(2L).orElseThrow(() -> new IllegalStateException("User not found"));
        CartItem cartItem = cartItemRepository.findById(2L).orElseThrow(() -> new IllegalStateException("CartItem not found"));

        // When / Then: 잔액 부족 예외 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), null);
        });
        assertEquals("Insufficient balance.", exception.getMessage());
    }

    @Test
    @DisplayName("주문 결제 실패 - 쿠폰 만료")
    void payOrder_CouponExpired() {
        // Given: 만료된 쿠폰
        User user = userRepository.findById(2L).orElseThrow(() -> new IllegalStateException("User not found"));
        CartItem cartItem = cartItemRepository.findById(2L).orElseThrow(() -> new IllegalStateException("CartItem not found"));

        // When / Then: 만료된 쿠폰 예외 확인
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            orderApplicationServiceImpl.payOrder(List.of(cartItem.getId()), 2L);
        });
        assertEquals("Invalid or expired coupon.", exception.getMessage());
    }
}
