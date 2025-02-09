package kr.hhplus.be.server.controller.balance;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.domain.user.repository.UserRepository;
import kr.hhplus.be.server.service.balance.BalanceHistoryService;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@AutoConfigureMockMvc
@SpringBootTest
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BalanceHistoryService balanceHistoryService;

    @Autowired
    private UserRepository userRepository;

    private static final MySQLContainer<?> MYSQL_CONTAINER = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql");

    static {
        MYSQL_CONTAINER.start();
    }

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
    }

    @Test
    @DisplayName("잔액 충전 성공")
    void chargeBalance_Success() throws Exception {
        // Given
        BalanceChargeVO request = BalanceChargeVO.builder()
                .userId(1L)
                .amount(BigDecimal.valueOf(500.00))
                .build();

        // When & Then
        mockMvc.perform(post("/api/v1/balance/charge/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId", is(1)))
                .andExpect(jsonPath("$.data.amount", is(500.00)))
                .andExpect(jsonPath("$.data.currentBalance", is(5500.00)));
    }

    @Test
    @DisplayName("잔액 조회 성공")
    void getBalance_Success() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/balance/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId", is(1))) // userId 확인
                .andExpect(jsonPath("$.data.currentBalance", is(5000.0))); // currentBalance 확인
    }

    @Test
    @DisplayName("잔액 조회 실패 - 사용자 없음")
    void getBalance_Fail_UserNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/balance/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is("존재하지 않는 사용자입니다."))) // 수정: ErrorCode 메시지 반영
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("잔액 동시성 테스트: 충전 및 사용 요청 동시 실행")
    void balanceConcurrencyTest() throws InterruptedException {
        // 초기 사용자 및 데이터 설정
        Long userId = 1L; // 테스트 사용자 ID
        User user = userRepository.findById(userId).orElseThrow(); // 사용자 조회
        user.setBalance(BigDecimal.ZERO); // 초기 잔액 0으로 설정

        BigDecimal chargeAmount = BigDecimal.valueOf(100); // 충전 금액
        BigDecimal useAmount = BigDecimal.valueOf(50); // 사용 금액
        int totalRequests = 20; // 총 요청 수 (충전 10, 사용 10)
        BigDecimal expectedFinalBalance = BigDecimal.valueOf(6000.00); // 예상 최종 잔액 (충전 100 x 10 - 사용 50 x 10)
        BigDecimal scaledExpected = expectedFinalBalance.setScale(2, RoundingMode.HALF_UP);


        // 동시 요청 실행 준비
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(totalRequests);

        for (int i = 0; i < totalRequests; i++) {
            boolean isCharge = i % 2 == 0; // 짝수 요청은 충전, 홀수 요청은 사용
            executorService.submit(() -> {
                try {
                    if (isCharge) {
                        // 충전 요청
                        BalanceChargeVO chargeVO = BalanceChargeVO.builder()
                                .amount(chargeAmount)
                                .build();
                        balanceHistoryService.chargeBalance(chargeVO, user);
                    } else {
                        // 사용 요청
                        balanceHistoryService.use(user, useAmount);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }

        // 모든 요청이 종료될 때까지 대기
        latch.await();
        executorService.shutdown();

        // 최종 잔액 검증
        BigDecimal finalBalance = balanceHistoryService.calculate(user);
        assertEquals(scaledExpected, finalBalance, "최종 잔액이 예상과 다릅니다.");
    }

}
