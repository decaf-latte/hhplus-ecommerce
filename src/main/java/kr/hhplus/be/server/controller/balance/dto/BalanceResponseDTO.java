package kr.hhplus.be.server.controller.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BalanceResponseDTO {
    private long userId;
    private BigDecimal currentBalance;

    @Builder
    public BalanceResponseDTO(long userId, BigDecimal currentBalance) {
        this.userId = userId;
        this.currentBalance = currentBalance;
    }
}

