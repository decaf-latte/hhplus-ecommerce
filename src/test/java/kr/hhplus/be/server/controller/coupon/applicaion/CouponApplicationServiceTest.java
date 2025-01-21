package kr.hhplus.be.server.controller.coupon.applicaion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import kr.hhplus.be.server.controller.coupon.application.CouponApplicationServiceImpl;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.coupon.UserCouponService;
import kr.hhplus.be.server.service.coupon.vo.UserCouponVO;
import kr.hhplus.be.server.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class CouponApplicationServiceTest {

    @Container
    private static final MySQLContainer<?> mysqlContainer = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("hhplus")
            .withUsername("application")
            .withPassword("application")
            .withInitScript("init.sql");


    @Autowired
    private CouponApplicationServiceImpl couponApplicationService;

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("쿠폰 코드로 쿠폰 발급 성공 - 통합 테스트")
    void issueCouponByCode_success() {
        // Given
        long userId = 2;
        String couponCode = "DISCOUNT10";

        User user = userService.getUserById(userId).orElseThrow();

        // When
        couponApplicationService.issueCouponByCode(userId, couponCode);

        // Then
        List<UserCoupon> userCoupons = userCouponService.getUserCoupons(user);
        System.out.println("User Coupons: " + userCoupons);
        assertFalse(userCoupons.isEmpty());
        assertEquals(2, userCoupons.size());
    }

    @Test
    @DisplayName("사용자 조회 실패 - 통합 테스트")
    void issueCouponByCode_userNotFound() {
        // Given
        long invalidUserId = 999L;
        String couponCode = "DISCOUNT10";

        // When / Then
        assertThrows(CommerceUserException.class, () ->
                couponApplicationService.issueCouponByCode(invalidUserId, couponCode)
        );
    }

    @Test
    @DisplayName("쿠폰 조회 실패 - 통합 테스트")
    void issueCouponByCode_couponNotFound() {
        // Given
        long userId = 1;
        String invalidCouponCode = "INVALID123";

        // When / Then
        assertThrows(CommerceCouponException.class, () ->
                couponApplicationService.issueCouponByCode(userId, invalidCouponCode)
        );
    }

    @Test
    @DisplayName("사용자 보유 쿠폰 목록 조회 성공 - 통합 테스트")
    void getUserCoupons_success() {
        // Given: init.sql에서 id가 1인 사용자와 이미 발급된 쿠폰이 존재함
        long userId = 1L;

        // When
        List<UserCouponVO> userCoupons = couponApplicationService.getUserCoupons(userId);

        // Then
        assertFalse(userCoupons.isEmpty());
        assertEquals(1, userCoupons.size());
    }

    @Test
    @DisplayName("사용자 보유 쿠폰 목록 조회 실패 - 사용자 없음")
    void getUserCoupons_userNotFound() {
        // Given
        long invalidUserId = 999L;

        // When / Then
        assertThrows(CommerceUserException.class, () ->
                couponApplicationService.getUserCoupons(invalidUserId)
        );
    }
}

