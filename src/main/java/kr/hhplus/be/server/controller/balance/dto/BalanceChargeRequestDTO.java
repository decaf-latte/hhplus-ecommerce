package kr.hhplus.be.server.controller.balance.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BalanceChargeRequestDTO {
    private long userId;
    private int amount;

    @Builder
    public BalanceChargeRequestDTO(long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }
}
