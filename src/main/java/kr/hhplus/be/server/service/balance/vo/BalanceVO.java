package kr.hhplus.be.server.service.balance.vo;

import kr.hhplus.be.server.domain.balance.code.BalanceType;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BalanceVO {

    private User user;
    private BigDecimal changeAmount;
    private BalanceType type;
    private BigDecimal currentBalance;

    @Builder
    public BalanceVO(User user, BigDecimal changeAmount, BalanceType type, BigDecimal currentBalance) {
        this.user = user;
        this.changeAmount = changeAmount;
        this.type = type;
        this.currentBalance = currentBalance;
    }
}
