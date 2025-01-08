package kr.hhplus.be.server.service.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;

import java.util.Optional;

public interface CouponService {

    Optional<Coupon> getCouponByCode(String code);

    void issueCoupon(Coupon coupon);
}
