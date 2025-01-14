package kr.hhplus.be.server.service.balance.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BalanceChargeVO {

    private long userId;
    private BigDecimal amount;

    @Builder
    public BalanceChargeVO(long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
