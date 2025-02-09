package kr.hhplus.be.server.controller.coupon.application;

import java.util.List;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.coupon.CouponService;
import kr.hhplus.be.server.service.coupon.UserCouponService;
import kr.hhplus.be.server.service.coupon.vo.UserCouponVO;
import kr.hhplus.be.server.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponApplicationServiceImpl implements CouponApplicationService {

  private final CouponService couponService;
  private final UserCouponService userCouponService;
  private final UserService userService;

  private final RedisTemplate<String, String> redisTemplate;
  private static final String COUPON_REQUEST_KEY_PREFIX = "coupon-requests:";
  private static final String COUPON_ISSUED_KEY_PREFIX = "coupon-issued:";


  // 쿠폰 코드로 쿠폰 발급
  @Override
  @Transactional
  public void issueCouponByCode(long userId, String couponCode) {

    String requestKey = COUPON_REQUEST_KEY_PREFIX + couponCode;
    String issuedKey = COUPON_ISSUED_KEY_PREFIX + couponCode;

    // 1 중복 발급 체크 (사용자가 이미 쿠폰을 받았는지 확인)
    Boolean isAlreadyIssued = redisTemplate.opsForSet().isMember(issuedKey, String.valueOf(userId));
    if (Boolean.TRUE.equals(isAlreadyIssued)) {
      throw new CommerceCouponException(ErrorCode.COUPON_ALREADY_ISSUED);
    }

    // 2 요청 시간(Score) 기록 - 현재 Unix Timestamp 사용
    long score = System.currentTimeMillis();
    redisTemplate.opsForZSet().add(requestKey, String.valueOf(userId), score);

    log.info("쿠폰 발급 요청 등록: userId={}, couponCode={}, score={}", userId, couponCode, score);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserCouponVO> getUserCoupons(Long userId) {

    // 사용자 조회
    User user =
        userService
            .getUserById(userId)
            .orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));

    // 사용자 보유 쿠폰 목록 조회
    List<UserCoupon> userCoupons = userCouponService.getUserCoupons(user);

    return userCoupons.stream().map(UserCouponVO::from).toList();
  }
}
