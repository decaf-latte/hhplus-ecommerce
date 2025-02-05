package kr.hhplus.be.server.domain.order.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId")
    List<Order> findByUserIdWithLock(Long userId);
}
