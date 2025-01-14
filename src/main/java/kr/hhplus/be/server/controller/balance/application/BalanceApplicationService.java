package kr.hhplus.be.server.controller.balance.application;

import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;
import kr.hhplus.be.server.service.balance.vo.BalanceVO;
import kr.hhplus.be.server.service.user.vo.UserVO;

import java.math.BigDecimal;

public interface BalanceApplicationService {

    BalanceVO chargeBalance(BalanceChargeVO chargeVO);

    UserVO getUser(Long userId);

    void updateBalance(User user, BigDecimal resultBalance);


}
