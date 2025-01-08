package kr.hhplus.be.server.service.payment;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentCoupon;

public interface PaymentCouponService {

    PaymentCoupon save(Payment payment, UserCoupon userCoupon, BigDecimal amount);
}
