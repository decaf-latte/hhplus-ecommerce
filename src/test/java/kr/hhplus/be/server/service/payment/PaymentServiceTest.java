package kr.hhplus.be.server.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.repository.PaymentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("결제 내역 저장 성공")
    void savePaymentSuccess() {
        Order order = Order.of().build();
        BigDecimal amount = new BigDecimal("100");
        Payment payment = Payment.of().order(order).amount(amount).build();

        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        Payment savedPayment = paymentService.save(order, amount);

        assertThat(savedPayment).isNotNull();
        assertThat(savedPayment.getOrder()).isEqualTo(order);
        assertThat(savedPayment.getAmount()).isEqualTo(amount);
    }

    @Test
    @DisplayName("결제 내역 저장 실패 - 금액이 null")
    void savePaymentFailureAmountNull() {
        Order order = Order.of().build();
        BigDecimal amount = null;

        Payment savedPayment = paymentService.save(order, amount);

        assertThat(savedPayment).isNull();
    }

    @Test
    @DisplayName("결제 내역 저장 실패 - 주문이 null")
    void savePaymentFailureOrderNull() {
        Order order = null;
        BigDecimal amount = new BigDecimal("100");

        Payment savedPayment = paymentService.save(order, amount);

        assertThat(savedPayment).isNull();
    }
}