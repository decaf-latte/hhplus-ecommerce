package kr.hhplus.be.server.service.coupon;

import java.time.LocalDateTime;
import java.util.Optional;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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
            throw new CommerceCouponException(ErrorCode.INVALID_COUPON_REGISTRATION_PERIOD);
        }

        if (coupon.getStock() <= 0) {
            throw new CommerceCouponException(ErrorCode.INSUFFICIENT_COUPON_STOCK);
        }
    }
}
