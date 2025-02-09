package kr.hhplus.be.server.scheduler.coupon;

import kr.hhplus.be.server.scheduler.coupon.application.CouponSchedulerApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CouponScheduler {

  private final CouponSchedulerApplicationService couponSchedulerApplicationService;


  @Scheduled(fixedRate = 5000)
  public void processCouponRequests() {
    // 5초마다 실행
    couponSchedulerApplicationService.processCouponRequests();

  }

}
