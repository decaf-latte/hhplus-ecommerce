package kr.hhplus.be.server.domain.outbox.repository;

import kr.hhplus.be.server.domain.outbox.code.OutboxEventStatus;
import kr.hhplus.be.server.domain.outbox.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, Long> {

    List<OutboxEvent> findByStatus(OutboxEventStatus status);

    Optional<OutboxEvent> findByAggregateId(Long aggregateId);

}
