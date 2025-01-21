package kr.hhplus.be.server.controller.balance;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import kr.hhplus.be.server.controller.balance.application.BalanceApplicationService;
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
    private BalanceApplicationService balanceApplicationService;

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
}
