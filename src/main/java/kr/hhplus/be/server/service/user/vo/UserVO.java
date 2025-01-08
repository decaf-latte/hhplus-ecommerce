package kr.hhplus.be.server.service.user.vo;

import kr.hhplus.be.server.controller.balance.dto.BalanceResponseDTO;
import kr.hhplus.be.server.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class UserVO {

    private Long id;
    private String name;
    private String email;
    private BigDecimal balance;

    @Builder
    public UserVO(Long id, String name, String email, BigDecimal balance) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.balance = balance;
    }

    public static UserVO from(User user) {

        if (ObjectUtils.isEmpty(user)) {
            return null;
        }

        return UserVO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .balance(user.getBalance())
                .build();
    }

    public BalanceResponseDTO toBalanceResponseDTO() {
        return BalanceResponseDTO.builder()
                .userId(this.id)
                .currentBalance(this.balance)
                .build();
    }


}
