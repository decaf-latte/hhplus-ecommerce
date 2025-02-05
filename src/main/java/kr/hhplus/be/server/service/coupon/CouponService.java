package kr.hhplus.be.server.service.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.service.coupon.vo.CouponVO;

import java.util.List;
import java.util.Optional;

public interface CouponService {

    Optional<Coupon> getCouponByCode(String code);

    void issueCoupon(Coupon coupon);

    int getAvailableCouponCount(String code);

    List<CouponVO> getAvailableCoupons();
}
