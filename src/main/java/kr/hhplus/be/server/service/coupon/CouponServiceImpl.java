package kr.hhplus.be.server.service.coupon;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    @Override
    public Optional<Coupon> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    @Override
    public void issueCoupon(Coupon coupon) {

        validIssueCoupon(coupon);

        coupon.issueCoupon();
        couponRepository.save(coupon);
    }

    private static void validIssueCoupon(Coupon coupon) {
        LocalDateTime now = LocalDateTime.now();

        if (now.isBefore(coupon.getRegisterStartDate()) || now.isAfter(coupon.getRegisterEndDate())) {
            throw new IllegalArgumentException("Coupon registration period is not valid.");
        }

        if (coupon.getStock() <= 0) {
            throw new IllegalArgumentException("Coupon stock is insufficient.");
        }
    }
}
