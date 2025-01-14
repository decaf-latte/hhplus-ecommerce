package kr.hhplus.be.server.controller.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChargeRequestDTO {
    private long userId;
    private BigDecimal amount;

    @Builder
    public BalanceChargeRequestDTO(long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
