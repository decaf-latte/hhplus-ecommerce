package kr.hhplus.be.server.domain.cart.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM CartItem c WHERE c.id IN :cartItemIds")
    List<CartItem> findByUserWithLock(@Param("cartItemIds") List<Long> cartItemIds);
}
