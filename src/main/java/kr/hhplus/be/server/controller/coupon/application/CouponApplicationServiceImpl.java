package kr.hhplus.be.server.controller.coupon.application;

import java.util.List;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.coupon.CouponService;
import kr.hhplus.be.server.service.coupon.UserCouponService;
import kr.hhplus.be.server.service.coupon.vo.UserCouponVO;
import kr.hhplus.be.server.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponApplicationServiceImpl implements CouponApplicationService {

    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final UserService userService;

    // 쿠폰 코드로 쿠폰 발급
    @Override
    @Transactional
    public void issueCouponByCode(long userId, String couponCode) {

        // 사용자 조회
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));

        // 쿠폰 조회
        Coupon coupon = couponService.getCouponByCode(couponCode)
                .orElseThrow(() -> new CommerceCouponException(ErrorCode.COUPON_NOT_EXIST));

        // 쿠폰 발급
        couponService.issueCoupon(coupon);
        userCouponService.issueCoupon(user, coupon);

        log.info("issueCouponByCode userId: {}, couponCode: {}", userId, couponCode);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCouponVO> getUserCoupons(Long userId) {

        // 사용자 조회
        User user = userService.getUserById(userId)
            .orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));

        // 사용자 보유 쿠폰 목록 조회
        List<UserCoupon> userCoupons = userCouponService.getUserCoupons(user);

        return userCoupons.stream().map(UserCouponVO::from).toList();
    }
}

