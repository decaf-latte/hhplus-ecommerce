package kr.hhplus.be.server.controller.coupon;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import kr.hhplus.be.server.controller.coupon.application.CouponApplicationService;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.domain.common.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerConcurrentTest {

    @Autowired
    private MockMvc mockMvc;  // MockMvc 주입

    @Mock
    private CouponApplicationService couponApplicationService;  // 쿠폰 발급 서비스 모킹

    @InjectMocks
    private CouponController couponController;  // 실제 쿠폰 컨트롤러에 모킹된 서비스 주입

    @Test
    @DisplayName("쿠폰 발급 동시성 테스트 : 쿠폰 재고5, 발급신청 8, 성공 5, 실패 3")
    void testCouponIssueConcurrency() throws Exception {
        List<Long> userIds = List.of(4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L);  // 8명 사용자
        String couponCode = "DISCOUNT30";

        // 쿠폰 발급 서비스 모킹: 5번은 성공, 3번은 실패
        doNothing().when(couponApplicationService).issueCouponByCode(eq(userIds.get(0)), eq(couponCode));  // 성공
        doNothing().when(couponApplicationService).issueCouponByCode(eq(userIds.get(1)), eq(couponCode));  // 성공
        doNothing().when(couponApplicationService).issueCouponByCode(eq(userIds.get(2)), eq(couponCode));  // 성공
        doNothing().when(couponApplicationService).issueCouponByCode(eq(userIds.get(3)), eq(couponCode));  // 성공
        doNothing().when(couponApplicationService).issueCouponByCode(eq(userIds.get(4)), eq(couponCode));  // 성공

        doThrow(new CommerceCouponException(ErrorCode.INSUFFICIENT_COUPON_STOCK)).when(couponApplicationService).issueCouponByCode(eq(userIds.get(5)), eq(couponCode));  // 실패
        doThrow(new CommerceCouponException(ErrorCode.INSUFFICIENT_COUPON_STOCK)).when(couponApplicationService).issueCouponByCode(eq(userIds.get(6)), eq(couponCode));  // 실패
        doThrow(new CommerceCouponException(ErrorCode.INSUFFICIENT_COUPON_STOCK)).when(couponApplicationService).issueCouponByCode(eq(userIds.get(7)), eq(couponCode));  // 실패

        // 비동기 요청 처리
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        List<Integer> statuses = new ArrayList<>();  // 응답 상태 코드 저장

        // 비동기적으로 각 요청 처리
        for (Long userId : userIds) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/v1/coupons/issue")
                                    .contentType("application/json")
                                    .content(String.format("{\"userId\": %d, \"couponCode\": \"%s\"}", userId, couponCode)))
                            .andReturn();

                    // 상태 코드를 리스트에 추가
                    synchronized (statuses) {
                        statuses.add(result.getResponse().getStatus());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, executorService);

            futures.add(future);
        }

        // 모든 비동기 작업이 완료될 때까지 대기
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // 모든 상태 코드가 저장된 상태로 검증
        // 5번은 200, 3번은 422여야 함
        long successCount = statuses.stream().filter(status -> status == 200).count();
        long failureCount = statuses.stream().filter(status -> status == 422).count();

        assertEquals(5, successCount, "성공한 요청은 5개여야 합니다.");
        assertEquals(3, failureCount, "실패한 요청은 3개여야 합니다.");

        // ExecutorService 종료
        executorService.shutdown();
    }

}
