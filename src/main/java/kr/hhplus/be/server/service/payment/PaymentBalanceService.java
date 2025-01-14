package kr.hhplus.be.server.service.payment;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentBalance;

public interface PaymentBalanceService {

    PaymentBalance save(Payment payment, BalanceHistory balanceHistory, BigDecimal amount);
}
