package kr.hhplus.be.server.controller.balance.application;

import jakarta.persistence.EntityNotFoundException;
import kr.hhplus.be.server.domain.balance.code.BalanceType;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.balance.BalanceHistoryService;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;
import kr.hhplus.be.server.service.balance.vo.BalanceVO;
import kr.hhplus.be.server.service.user.UserService;
import kr.hhplus.be.server.service.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BalanceApplicationServiceImpl implements BalanceApplicationService {

    private final BalanceHistoryService balanceService;
    private final UserService userService;


    @Override
    public BalanceVO chargeBalance(BalanceChargeVO chargeVO) {

        User user = userService.getUserById(chargeVO.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        //최종 잔액
        BigDecimal resultBalance = balanceService.chargeBalance(chargeVO,user);

        updateBalance(user, resultBalance);

        return BalanceVO.builder()
                .user(user)
                .changeAmount(chargeVO.getAmount())
                .type(BalanceType.CHARGE)
                .currentBalance(resultBalance)
                .build();
    }

    @Override
    public UserVO getUser(Long userId) {

        User user = userService.getUserById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found."));

        return UserVO.builder()
                .id(user.getId())
                .name(user.getName())
                .balance(user.getBalance())
                .build();
    }

    @Override
    public void updateBalance(User user, BigDecimal resultBalance) {
        user.updateBalance(resultBalance);
    }

}
