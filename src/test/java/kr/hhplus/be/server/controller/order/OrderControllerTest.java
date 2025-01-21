package kr.hhplus.be.server.controller.order;


import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import kr.hhplus.be.server.controller.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.domain.order.code.OrderStatus;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderControllerTest {

    private static MockWebServer mockWebServer;


    @Container
    static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("init.sql");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

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
    static void configurePropertiesRegistry(DynamicPropertyRegistry registry) {
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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("주문 성공 - 쿠폰 사용 O")
    void payOrderSuccess_useCoupon() throws Exception {
        // Given: 카트 아이템과 쿠폰 ID 설정
        List<Long> cartItemIds = List.of(1L, 2L);
        Long userCouponId = 1L;

        OrderRequestDTO requestDTO = new OrderRequestDTO(cartItemIds, userCouponId);

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is(OrderStatus.COMPLETED.name())))
                .andExpect(jsonPath("$.data.totalPrice", is(3200.0)))
                .andExpect(jsonPath("$.data.payment.totalAmount", is(3200.0)))
                .andExpect(jsonPath("$.data.payment.couponDiscountAmount", is(320))) // 쿠폰 할인 금액
                .andExpect(jsonPath("$.data.payment.balanceAmount", is(2880.0))); // 잔액 사용 금액 (총 금액 - 쿠폰 할인)
    }

    @Test
    @DisplayName("주문 성공 - 쿠폰 사용 X")
    void payOrderSuccess_notUseCoupon() throws Exception {
        // Given: init.sql 데이터 기준으로 카트 아이템 설정
        List<Long> cartItemIds = List.of(3L, 4L);

        // OrderRequestDTO 생성
        OrderRequestDTO requestDTO = new OrderRequestDTO(cartItemIds, null);

        // When & Then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is(OrderStatus.COMPLETED.name()))) // 주문 상태 확인
                .andExpect(jsonPath("$.data.totalPrice", is(3200.0)))
                .andExpect(jsonPath("$.data.payment.totalAmount", is(3200.0))) // 총 결제 금액 확인
                .andExpect(jsonPath("$.data.payment.couponDiscountAmount", is(0))) // 쿠폰 할인 없음
                .andExpect(jsonPath("$.data.payment.balanceAmount", is(3200.0))); // 잔액 결제 확인
    }
}