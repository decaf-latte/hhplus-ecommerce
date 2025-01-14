package kr.hhplus.be.server.service.balance;

import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;

import java.math.BigDecimal;

public interface BalanceHistoryService {

    BigDecimal chargeBalance(BalanceChargeVO chargeVO, User user);

    BigDecimal calculate(User user);

    BalanceHistory use(User user, BigDecimal amount);
}
