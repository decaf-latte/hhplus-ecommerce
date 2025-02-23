package kr.hhplus.be.server.domain.outbox.entity;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.outbox.code.OutboxEventStatus;
import kr.hhplus.be.server.event.order.PaidOrderEvent;
import kr.hhplus.be.server.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_event")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long aggregateId;  // 주문 ID
    private String aggregateType; // ORDER
    private String eventType; // PaidOrderEvent
    private String payload; // 이벤트 내용 (JSON)
    @Enumerated(EnumType.STRING)
    private OutboxEventStatus status = OutboxEventStatus.PENDING; // 기본값
    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static OutboxEvent of(PaidOrderEvent event) {
        return OutboxEvent.builder()
                .aggregateId(event.getOrderId())
                .aggregateType("ORDER")
                .eventType("PaidOrderEvent")
                .payload(JsonUtil.convertToJson(event))
                .status(OutboxEventStatus.PENDING)
                .build();
    }

    public void markAsProcessed() {
        this.status = OutboxEventStatus.PUBLISHED;
    }
}

