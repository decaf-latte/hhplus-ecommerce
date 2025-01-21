package kr.hhplus.be.server.service.coupon;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserCouponServiceTest {

    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private UserCouponServiceImpl userCouponService;

    @Test
    @DisplayName("사용자에게 쿠폰을 성공적으로 발행")
    void issueCoupon_Success() {
        User user = User.of().name("Tester").build(); // ID 설정
        Coupon coupon = Coupon.of().availableDay(30).build(); // ID 설정
        when(userCouponRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.empty());

        userCouponService.issueCoupon(user, coupon);

        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("이미 발급된 쿠폰을 사용자에게 발행 시 예외 발생")
    void issueCoupon_AlreadyIssued() {
        User user = User.of().build(); // ID 설정
        Coupon coupon = Coupon.of().build(); // ID 설정
        UserCoupon existingUserCoupon = UserCoupon.of().build();

        when(userCouponRepository.findByIdAndUserId(any(), any()))
                .thenReturn(Optional.of(existingUserCoupon));

        CommerceCouponException exception = assertThrows(CommerceCouponException.class, () -> {
            userCouponService.issueCoupon(user, coupon);
        });

        assertEquals("이미 발급된 쿠폰입니다.", exception.getMessage());
    }


    @Test
    @DisplayName("사용자의 모든 쿠폰을 성공적으로 조회")
    void getUserCoupons_Success() {
        User user = User.of().build();
        List<UserCoupon> userCoupons = List.of(UserCoupon.of().build());
        when(userCouponRepository.findByUser(user)).thenReturn(userCoupons);

        List<UserCoupon> result = userCouponService.getUserCoupons(user);

        assertEquals(userCoupons.size(), result.size());
    }

    @Test
    @DisplayName("쿠폰 사용 성공")
    void useCoupon_Success() {
        UserCoupon userCoupon = UserCoupon.of().status(CouponStatus.ACTIVE).build();
        when(userCouponRepository.save(userCoupon)).thenReturn(userCoupon);

        UserCoupon result = userCouponService.use(userCoupon);

        assertEquals(CouponStatus.USED, result.getStatus());
        verify(userCouponRepository, times(1)).save(userCoupon);
    }
}