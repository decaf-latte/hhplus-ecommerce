package kr.hhplus.be.server.service.balance;

import kr.hhplus.be.server.domain.balance.code.BalanceType;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.balance.repository.BalanceHistoryRepository;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceHistoryServiceImpl implements BalanceHistoryService {

    private final BalanceHistoryRepository balanceHistoryRepository;

    @Override
    public BigDecimal chargeBalance(BalanceChargeVO chargeVO, User user) {

        BalanceHistory balanceHistory = BalanceHistory.of()
                .user(user)
                .amount(chargeVO.getAmount())
                .type(BalanceType.CHARGE)
                .build();

        balanceHistoryRepository.save(balanceHistory);

        BigDecimal currentBalance = Optional.ofNullable(user.getBalance())
                .orElse(BigDecimal.ZERO);

        return currentBalance.add(chargeVO.getAmount());
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BigDecimal calculate(User user) {

        // 사용자와 관련된 잔액 내역 조회
        List<BalanceHistory> balanceHistories = balanceHistoryRepository.findByUser(user);
        if (balanceHistories.isEmpty()) {
            return BigDecimal.ZERO;
        }

        // 잔액 타입별 초기 금액 설정
        Map<BalanceType, BigDecimal> balanceTypeAmountMap = initializeBalanceMap();

        // 각 잔액 타입별 금액 합산
        balanceHistories.forEach(balanceHistory ->
                balanceTypeAmountMap.merge(balanceHistory.getType(), balanceHistory.getAmount(), BigDecimal::add)
        );

        // 사용 가능 잔액 계산 (충전 금액 - 사용 금액 - 환불 금액)
        BigDecimal availableBalance = balanceTypeAmountMap.get(BalanceType.CHARGE)
                .subtract(balanceTypeAmountMap.get(BalanceType.USE))
                .subtract(balanceTypeAmountMap.get(BalanceType.REFUND));

        // 사용자 객체에 잔액 업데이트
        user.setBalance(availableBalance);

        return availableBalance;
    }

    @Override
    @Transactional
    public BalanceHistory use(User user, BigDecimal amount) {
        // 잔액 사용 내역 생성
        BalanceHistory balanceHistory = BalanceHistory.of()
                .user(user)
                .type(BalanceType.USE)
                .amount(amount)
                .build();

        // 사용자 잔액 차감
        user.setBalance(user.getBalance().subtract(amount));

        // 잔액 내역 저장
        return balanceHistoryRepository.save(balanceHistory);
    }

    private Map<BalanceType, BigDecimal> initializeBalanceMap() {
        return Arrays.stream(BalanceType.values())
                .collect(Collectors.toMap(balanceType -> balanceType, balanceType -> BigDecimal.ZERO));
    }
}
