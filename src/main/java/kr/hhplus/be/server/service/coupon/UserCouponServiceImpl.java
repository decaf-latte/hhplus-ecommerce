package kr.hhplus.be.server.service.coupon;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.coupon.repository.UserCouponRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Override
    @Transactional
    public void issueCoupon(User user, Coupon coupon) {

        // 이미 발급된 쿠폰인지 확인
        getUserCouponByCouponIdAndUserId(coupon.getId(), user.getId())
                .ifPresent(userCoupon -> {
                    throw new IllegalArgumentException("User already has the coupon.");
                });

        LocalDateTime expiredAt = LocalDateTime.now();
        if (ObjectUtils.isEmpty(coupon.getAvailableDay())) {

            // 999년을 더해서 무제한 처리
            expiredAt = expiredAt.plusYears(999);
        } else {
            expiredAt = expiredAt.plusDays(coupon.getAvailableDay());
        }

        UserCoupon userCoupon = UserCoupon.of()
                .user(user)
                .coupon(coupon)
                .status(CouponStatus.ACTIVE)
                .expiredAt(expiredAt)
                .build();

        userCouponRepository.save(userCoupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCoupon> getUserCoupons(User user) {
        return userCouponRepository.findByUser(user);
    }

    @Override
    public Optional<UserCoupon> getUserCouponByCouponIdAndUserId(Long userCouponId, Long userId) {
        return userCouponRepository.findByIdAndUserId(userCouponId, userId);
    }

    @Override
    public UserCoupon use(UserCoupon userCoupon) {
        userCoupon.use();
        return userCouponRepository.save(userCoupon);
    }
}
