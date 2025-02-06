package kr.hhplus.be.server.scheduler.coupon.application;

import java.util.Set;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.coupon.CouponService;
import kr.hhplus.be.server.service.coupon.UserCouponService;
import kr.hhplus.be.server.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CouponSchedulerApplicationServiceImpl implements CouponSchedulerApplicationService {

    private final CouponService couponService;
    private final UserCouponService userCouponService;
    private final UserService userService;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String COUPON_REQUEST_KEY_PREFIX = "coupon-requests:";
    private static final String COUPON_ISSUED_KEY_PREFIX = "coupon-issued:";
    private static final String COUPON_FAILED_KEY_PREFIX = "coupon-failed:";

    @Override
    public void processCouponRequests() {

        log.info(">>>>> schedule start");

        // 1 Redis에서 쿠폰 요청(`ZSET`)이 있는 쿠폰 코드 목록 가져오기
        Set<String> couponCodes = redisTemplate.keys(COUPON_REQUEST_KEY_PREFIX + "*");

        if (couponCodes == null || couponCodes.isEmpty()) {
            log.warn(" Redis에서 조회된 쿠폰 요청이 없습니다.");
            return;
        }

        log.info(" Redis에서 조회된 쿠폰 코드 목록: {}", couponCodes);

        for (String requestKey : couponCodes) {
            String couponCode = requestKey.replace(COUPON_REQUEST_KEY_PREFIX, ""); // Key에서 쿠폰 코드 추출
            String issuedKey = COUPON_ISSUED_KEY_PREFIX + couponCode;
            String failedKey = COUPON_FAILED_KEY_PREFIX + couponCode;

            log.info(" 쿠폰 코드: {}", couponCode);


            // 2 DB에서 해당 쿠폰 코드가 존재하는지 확인
            Coupon coupon = couponService.getCouponByCode(couponCode)
                .orElseThrow(() -> new CommerceCouponException(ErrorCode.COUPON_NOT_AVAILABLE));

            // 3 DB에서 해당 쿠폰의 요청 가능 수량 조회
            int availableCouponCount = couponService.getAvailableCouponCount(couponCode);
            log.info(" 쿠폰 코드: {}, 요청 가능 수량: {}", couponCode, availableCouponCount);

            if (availableCouponCount <= 0) {
                log.error(" 쿠폰 소진됨: {}", couponCode);
                continue;
            }

            // 선착순에 못든 사람들 먼저 저장
            Set<String> failedUsers = redisTemplate.opsForZSet().range(requestKey, availableCouponCount, -1);

            // 4 Redis에서 선착순 요청 `N`개 가져오기 (`ZPOPMIN`)
            Set<ZSetOperations.TypedTuple<String>> users =
                redisTemplate.opsForZSet().popMin(requestKey, availableCouponCount);
            log.info(" 쿠폰 코드: {}, Redis에서 조회된 사용자: {}, 사용자 명수 : {}", couponCode, users,users.size());

            if (users.isEmpty()) {
                log.warn("쿠폰 요청한 사용자가 없습니다. (couponCode={})", couponCode);
                continue;
            }

            // 5 쿠폰 발급 처리
            for (ZSetOperations.TypedTuple<String> userTuple : users) {
                String userId = userTuple.getValue();

                User user = userService.getUserById(Long.valueOf(userId))
                    .orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));

                couponService.issueCoupon(coupon);
                userCouponService.issueCoupon(user, coupon);

                // 6 Redis 발급 이력 저장 (`SADD`)
                redisTemplate.opsForSet().add(issuedKey, userId);
                log.info("쿠폰 발급 완료: userId={}, couponCode={}", userId, couponCode);

            }

            // 7 선착순에서 밀린 사용자들 실패 목록에 저장
            if (!failedUsers.isEmpty()) {
                for (String failedUser : failedUsers) {
                    redisTemplate.opsForSet().add(failedKey, failedUser);
                }
                log.warn(" 선착순에서 밀린 {}명의 사용자 정보를 실패 목록에 저장 (couponCode={})", failedUsers.size(), couponCode);
                redisTemplate.opsForZSet().removeRange(requestKey, availableCouponCount, -1);
            }

            // 8 선착순에서 밀린 사용자들 요청 삭제
            Long removedCount = redisTemplate.opsForZSet().removeRange(requestKey, availableCouponCount, -1);
            if (removedCount != null && removedCount > 0) {
                log.warn(" 초과된 {}명의 요청을 추가로 삭제함 (couponCode={})", removedCount, couponCode);
                processFailedCouponRequests();
            }
        }

        log.info(">>>>> schedule end");

    }

    @Override
    public void processFailedCouponRequests() {

        log.info(">>>>> 쿠폰 발급 실패한 사용자에게 알림을 전송합니다.");

        Set<String> failedCouponKeys = redisTemplate.keys(COUPON_FAILED_KEY_PREFIX + "*");

        if (failedCouponKeys == null || failedCouponKeys.isEmpty()) {
            log.info("발급 실패한 사용자 목록이 없습니다.");
            return;
        }

        for (String failedKey : failedCouponKeys) {
            String couponCode = failedKey.replace(COUPON_FAILED_KEY_PREFIX, "");
            Set<String> failedUsers = redisTemplate.opsForSet().members(failedKey);

            if (failedUsers == null || failedUsers.isEmpty()) {
                continue;
            }

            for (String userId : failedUsers) {
                User user = userService.getUserById(Long.valueOf(userId))
                    .orElseThrow(() -> new CommerceUserException(ErrorCode.USER_NOT_EXIST));

//      todo 유저에게 발급 실패 알림 전송하기 -> 알림 서비스가 없어 로그로 대체
                log.info("[알림 전송] userId={}, couponCode={}", userId, couponCode);
            }

            // 알림을 보낸 후 Redis에서 해당 사용자 삭제
            redisTemplate.delete(failedKey);
        }
        log.info(">>>>> 발급 실패 알림 전송 완료.");
    }

}

