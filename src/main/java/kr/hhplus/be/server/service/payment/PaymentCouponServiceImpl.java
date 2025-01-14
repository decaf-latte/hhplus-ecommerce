package kr.hhplus.be.server.service.payment;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentCoupon;
import kr.hhplus.be.server.domain.payment.repository.PaymentCouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentCouponServiceImpl implements PaymentCouponService {

    private final PaymentCouponRepository paymentCouponRepository;

    @Override
    public PaymentCoupon save(Payment payment, UserCoupon userCoupon, BigDecimal amount) {
        PaymentCoupon paymentCoupon = PaymentCoupon.of()
                .payment(payment)
                .userCoupon(userCoupon)
                .amount(amount)
                .build();

        return paymentCouponRepository.save(paymentCoupon);
    }
}
