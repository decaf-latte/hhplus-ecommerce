package kr.hhplus.be.server.domain.payment.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "payment_balance")
public class PaymentBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", insertable = false, nullable = false)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "balance_history_id", nullable = false)
    private BalanceHistory balanceHistory;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Builder(builderMethodName = "of")
    public PaymentBalance(Payment payment, BalanceHistory balanceHistory, BigDecimal amount) {
        this.payment = payment;
        this.balanceHistory = balanceHistory;
        this.amount = amount;
    }
}

