package kr.hhplus.be.server.controller.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChargeResponseDTO {
    private long userId;
    private BigDecimal amount;
    private BigDecimal currentBalance;

    @Builder
    public BalanceChargeResponseDTO(long userId, BigDecimal amount,BigDecimal currentBalance) {
        this.userId = userId;
        this.amount = amount;
        this.currentBalance = currentBalance;
    }
}
