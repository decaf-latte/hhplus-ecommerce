package kr.hhplus.be.server.service.payment;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;

    // 결제 내역 저장
    @Override
    public Payment save(Order order, BigDecimal amount) {
        Payment payment = Payment.of()
                .order(order)
                .amount(amount)
                .build();

        return paymentRepository.save(payment);
    }
}
