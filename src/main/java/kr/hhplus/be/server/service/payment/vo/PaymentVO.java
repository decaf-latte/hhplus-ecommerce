package kr.hhplus.be.server.service.payment.vo;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor
public class PaymentVO {

    private Long id;
    private Long orderId;
    private BigDecimal amount;
    private PaymentCouponVO paymentCoupon;
    private PaymentBalanceVO paymentBalance;

    @Builder
    public PaymentVO(Long id, Long orderId, BigDecimal amount, PaymentCouponVO paymentCoupon, PaymentBalanceVO paymentBalance) {
        this.id = id;
        this.orderId = orderId;
        this.amount = amount;
        this.paymentCoupon = paymentCoupon;
        this.paymentBalance = paymentBalance;
    }

    public static PaymentVO from(Payment payment) {

        if (ObjectUtils.isEmpty(payment)) {
            return null;
        }

        return PaymentVO.builder()
                .id(payment.getId())
                .orderId(payment.getOrder().getId())
                .amount(payment.getAmount())
                .paymentCoupon(PaymentCouponVO.from(payment.getPaymentCoupon()))
                .paymentBalance(PaymentBalanceVO.from(payment.getPaymentBalance()))
                .build();
    }
}
