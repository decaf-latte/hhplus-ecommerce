package kr.hhplus.be.server.service.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.service.coupon.CouponServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepository couponRepository;

    @InjectMocks
    private CouponServiceImpl couponService;

    @Test
    @DisplayName("쿠폰 코드로 쿠폰을 성공적으로 조회")
    void getCouponByCode_Success() {
        String code = "TESTCODE";
        Coupon coupon = Coupon.of().code(code).build();
        when(couponRepository.findByCode(code)).thenReturn(Optional.of(coupon));

        Optional<Coupon> result = couponService.getCouponByCode(code);

        assertTrue(result.isPresent());
        assertEquals(code, result.get().getCode());
    }

    @Test
    @DisplayName("존재하지 않는 쿠폰 코드로 조회 시 빈 결과 반환")
    void getCouponByCode_NotFound() {
        String code = "INVALIDCODE";
        when(couponRepository.findByCode(code)).thenReturn(Optional.empty());

        Optional<Coupon> result = couponService.getCouponByCode(code);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("유효한 쿠폰을 성공적으로 발행")
    void issueCoupon_Success() {
        Coupon coupon = Coupon.of()
                .registerStartDate(LocalDateTime.now().minusDays(1))
                .registerEndDate(LocalDateTime.now().plusDays(1))
                .stock(10)
                .build();

        couponService.issueCoupon(coupon);

        verify(couponRepository, times(1)).save(coupon);
    }

    @Test
    @DisplayName("유효하지 않은 등록 기간으로 쿠폰 발행 시 예외 발생")
    void issueCoupon_InvalidRegistrationPeriod() {
        Coupon coupon = Coupon.of()
                .registerStartDate(LocalDateTime.now().plusDays(1))
                .registerEndDate(LocalDateTime.now().plusDays(2))
                .stock(10)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.issueCoupon(coupon);
        });

        assertEquals("Coupon registration period is not valid.", exception.getMessage());
    }

    @Test
    @DisplayName("재고가 부족한 쿠폰 발행 시 예외 발생")
    void issueCoupon_InsufficientStock() {
        Coupon coupon = Coupon.of()
                .registerStartDate(LocalDateTime.now().minusDays(1))
                .registerEndDate(LocalDateTime.now().plusDays(1))
                .stock(10)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            couponService.issueCoupon(coupon);
        });

        assertEquals("Coupon stock is insufficient.", exception.getMessage());
    }
}