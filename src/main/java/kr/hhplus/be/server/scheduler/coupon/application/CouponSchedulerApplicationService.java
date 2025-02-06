package kr.hhplus.be.server.scheduler.coupon.application;

public interface CouponSchedulerApplicationService {


    //발급 메소드
    void processCouponRequests();
    //실패 처리 메소드
    void processFailedCouponRequests();
}
