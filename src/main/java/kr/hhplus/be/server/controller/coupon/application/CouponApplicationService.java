package kr.hhplus.be.server.controller.coupon.application;

import kr.hhplus.be.server.service.coupon.vo.UserCouponVO;

import java.util.List;

public interface CouponApplicationService {

    void issueCouponByCode(long userId, String couponCode);

    List<UserCouponVO> getUserCoupons(Long userId);
}
