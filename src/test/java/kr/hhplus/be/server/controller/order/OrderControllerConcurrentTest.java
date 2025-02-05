package kr.hhplus.be.server.controller.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.hhplus.be.server.controller.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.domain.cart.repository.CartItemRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class OrderControllerConcurrentTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ObjectMapper objectMapper;

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
        registry.add("dataPlatformClient.url", () -> mockWebServer.url("/api").toString());
    }

    @BeforeAll
    static void setUpMockWebServer() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void tearDownMockWebServer() throws IOException {
        if (mockWebServer != null) {
            mockWebServer.shutdown();
        }
    }

    @BeforeEach
    void setUpMockResponses() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("true"));
    }

    @Test
    @DisplayName("주문 동시성 테스트: 상품 재고 2, 2명 사용자 구매 시도, 1 성공, 1 실패")
    void orderConcurrencyTest() throws Exception {
        Long cartItemId1 = 5L;
        Long cartItemId2 = 6L;

        Long userId1 = cartItemRepository.findById(cartItemId1).get().getUser().getId();
        Long userId2 = cartItemRepository.findById(cartItemId2).get().getUser().getId();

        // CountDownLatch를 사용하여 동시 실행 관리
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // 첫 번째 사용자의 주문 (성공)
        CompletableFuture<Void> orderTask1 = CompletableFuture.runAsync(() -> {
            try {
                OrderRequestDTO orderRequestDTO1 = OrderRequestDTO.builder()
                        .cartItemIds(List.of(cartItemId1))
                        .build();
                String content1 = objectMapper.writeValueAsString(orderRequestDTO1);

                mockMvc.perform(post("/api/v1/orders")
                                .contentType("application/json")
                                .content(content1))
                        .andExpect(status().isOk());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }, executorService);

        // 두 번째 사용자의 주문 (실패)
        CompletableFuture<Void> orderTask2 = CompletableFuture.runAsync(() -> {
            try {
                OrderRequestDTO orderRequestDTO2 = OrderRequestDTO.builder()
                        .cartItemIds(List.of(cartItemId2))
                        .build();
                String content2 = objectMapper.writeValueAsString(orderRequestDTO2);

                mockMvc.perform(post("/api/v1/orders")
                                .contentType("application/json")
                                .content(content2))
                        .andExpect(status().isBadRequest());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }, executorService);

        // 두 개의 주문을 동시에 실행
        CompletableFuture.allOf(orderTask1, orderTask2).join();

        // 모든 스레드가 완료될 때까지 대기
        latch.await();

        assertFalse(orderRepository.findByUserIdWithLock(userId1).isEmpty());
        assertTrue(orderRepository.findByUserIdWithLock(userId2).isEmpty());
    }


}
