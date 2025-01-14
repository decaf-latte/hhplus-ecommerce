package kr.hhplus.be.server.service.payment;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentBalance;
import kr.hhplus.be.server.domain.payment.repository.PaymentBalanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentBalanceServiceImpl implements PaymentBalanceService {

    private final PaymentBalanceRepository paymentBalanceRepository;

    @Override
    public PaymentBalance save(Payment payment, BalanceHistory balanceHistory, BigDecimal amount) {

        PaymentBalance paymentBalance = PaymentBalance.of()
                .payment(payment)
                .balanceHistory(balanceHistory)
                .amount(amount)
                .build();

        return paymentBalanceRepository.save(paymentBalance);
    }
}
