package kr.hhplus.be.server.controller.order;


import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import kr.hhplus.be.server.controller.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.domain.order.code.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
        List<Long> cartItemIds = List.of(1L, 2L); // Alice가 보유한 CartItem ID들 (Laptop, Smartphone)

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