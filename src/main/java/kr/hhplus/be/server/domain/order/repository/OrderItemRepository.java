package kr.hhplus.be.server.domain.order.repository;

import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.service.order.vo.TopOrderItemVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT new kr.hhplus.be.server.service.order.vo.TopOrderItemVO(oi.product.id, SUM(oi.quantity)) " +
            "FROM OrderItem oi " +
            "LEFT JOIN Order o ON oi.order.id = o.id " +
            "WHERE oi.createdAt >= :threeDaysAgo AND o.status = 'COMPLETED' " +
            "GROUP BY oi.product.id " +
            "ORDER BY SUM(oi.quantity) DESC")
    List<TopOrderItemVO> findTopOrderItemsLast3Days(@Param("threeDaysAgo") LocalDateTime threeDaysAgo, Pageable pageable);
}

