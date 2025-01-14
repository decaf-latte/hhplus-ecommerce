package kr.hhplus.be.server.domain.cart.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<CartItem> findByIdIn(List<Long> cartItemIds);
}
