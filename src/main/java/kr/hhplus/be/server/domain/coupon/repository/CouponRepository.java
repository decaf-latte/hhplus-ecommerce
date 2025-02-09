package kr.hhplus.be.server.domain.coupon.repository;

import jakarta.persistence.LockModeType;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("SELECT c FROM Coupon c WHERE c.code = :code")
    Optional<Coupon> findByCouponWithLock(@Param("code") String code);

    @Query("SELECT COUNT(c) FROM Coupon c WHERE c.code = :code AND c.stock > 0")
    int countAvailableCoupons(@Param("code") String couponCode);

    @Query("SELECT c.stock FROM Coupon c WHERE c.code = :couponCode")
    int getAvailableCouponCount(String couponCode);
}
