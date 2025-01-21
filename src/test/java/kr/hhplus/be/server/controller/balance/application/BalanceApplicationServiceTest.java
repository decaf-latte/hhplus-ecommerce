package kr.hhplus.be.server.controller.balance.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.balance.code.BalanceType;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;
import kr.hhplus.be.server.service.balance.vo.BalanceVO;
import kr.hhplus.be.server.service.user.vo.UserVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class BalanceApplicationServiceTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql"); // 데이터 초기화 스크립트

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mysqlContainer::getUsername);
        registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Autowired
    private BalanceApplicationServiceImpl balanceApplicationService;

    @Test
    @DisplayName("잔액 충전 성공")
    void chargeBalance_Success() {
        // Given
        Long userId = 1L;
        BigDecimal chargeAmount = BigDecimal.valueOf(500);
        BalanceChargeVO chargeVO = BalanceChargeVO.builder()
                .userId(userId)
                .amount(chargeAmount)
                .build();

        // When
        BalanceVO balanceVO = balanceApplicationService.chargeBalance(chargeVO);

        // Then
        assertNotNull(balanceVO);
        assertEquals(chargeAmount, balanceVO.getChangeAmount());
        assertEquals(BalanceType.CHARGE, balanceVO.getType());
        assertEquals(BigDecimal.valueOf(5500.00).setScale(2), balanceVO.getCurrentBalance().setScale(2));
    }

    @Test
    @DisplayName("사용자 조회 성공")
    void getUser_Success() {
        // Given
        Long userId = 1L;

        // When
        UserVO userVO = balanceApplicationService.getUser(userId);

        // Then
        assertNotNull(userVO);
        assertEquals(userId, userVO.getId());
        assertEquals("Alice", userVO.getName());
        assertEquals(BigDecimal.valueOf(5000.00).setScale(2), userVO.getBalance().setScale(2));
    }

    @Test
    @DisplayName("사용자 조회 실패 - 존재하지 않는 사용자")
    void getUser_NotFound() {
        // Given
        Long invalidUserId = 999L;

        // When / Then
        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(
                CommerceUserException.class,
                () -> balanceApplicationService.getUser(invalidUserId)
        );
        assertEquals("존재하지 않는 사용자입니다.", exception.getMessage());
    }
}
