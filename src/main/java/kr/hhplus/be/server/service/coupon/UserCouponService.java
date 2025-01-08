package kr.hhplus.be.server.service.coupon;

import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.entity.User;

public interface UserCouponService {

    void issueCoupon(User user, Coupon coupon);

    List<UserCoupon> getUserCoupons(User user);

    Optional<UserCoupon> getUserCouponByCouponIdAndUserId(Long userCouponId, Long userId);

    UserCoupon use(UserCoupon userCoupon);
}
