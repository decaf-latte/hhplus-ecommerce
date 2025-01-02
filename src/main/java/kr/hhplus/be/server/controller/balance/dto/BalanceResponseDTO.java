package kr.hhplus.be.server.controller.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceResponseDTO {
    private long userId;
    private int currentBalance;

    @Builder
    public BalanceResponseDTO(long userId, int currentBalance) {
        this.userId = userId;
        this.currentBalance = currentBalance;
    }
}

