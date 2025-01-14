package kr.hhplus.be.server.service.payment.vo;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.payment.entity.PaymentBalance;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

@Getter
@NoArgsConstructor
public class PaymentBalanceVO {

    private Long id;
    private BigDecimal amount;

    @Builder
    public PaymentBalanceVO(Long id, BigDecimal amount) {
        this.id = id;
        this.amount = amount;
    }

    public static PaymentBalanceVO from(PaymentBalance paymentBalance) {

        if (ObjectUtils.isEmpty(paymentBalance)) {
            return null;
        }

        return PaymentBalanceVO.builder()
                .id(paymentBalance.getId())
                .amount(paymentBalance.getAmount())
                .build();
    }
}
