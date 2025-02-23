package kr.hhplus.be.server.domain.outbox.code;

public enum OutboxEventStatus {
    PENDING,    // 아직 처리되지 않음
    PUBLISHED // Kafka로 성공적으로 전송됨
}

