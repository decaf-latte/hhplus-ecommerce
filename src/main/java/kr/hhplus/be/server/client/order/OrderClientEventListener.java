package kr.hhplus.be.server.client.order;

import kr.hhplus.be.server.client.DataPlatformService;
import kr.hhplus.be.server.event.order.PaidOrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderClientEventListener {

    private final DataPlatformService dataplatformService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void on(PaidOrderEvent paidOrderEvent) {
        try {
            dataplatformService.sendData(paidOrderEvent.getOrderId());
        } catch (Exception e) {
            log.error("DataPlatform 전송 실패 : {}", e.getMessage());
        }
    }
}
