package kr.hhplus.be.server.controller.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChargeResponseDTO {
    private long userId;
    private int amount;
    private int currentBalance;

    @Builder
    public BalanceChargeResponseDTO(long userId, int amount,int currentBalance) {
        this.userId = userId;
        this.amount = amount;
        this.currentBalance = currentBalance;
    }
}
