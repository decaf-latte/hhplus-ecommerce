package kr.hhplus.be.server.controller.coupon;

import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import kr.hhplus.be.server.controller.coupon.dto.CouponIssueRequestDTO;
import kr.hhplus.be.server.controller.coupon.dto.CouponIssueResponseDTO;
import kr.hhplus.be.server.controller.coupon.dto.CouponResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CouponMockController {

    //쿠폰 발급 기능 API - 성공
    @PostMapping("/api/v1/coupons/issue/mock")
    public ResponseDTO<CouponIssueResponseDTO> issueCouponSuccess(@RequestBody CouponIssueRequestDTO requestDTO) {
    return ResponseDTO.success(
        CouponIssueResponseDTO.builder()
            .couponId(1L)
            .couponCode(requestDTO.getCouponCode())
            .discount(1000)
            .issueDate(LocalDateTime.parse("2025-01-01T12:00:00Z"))
            .expireDate(LocalDateTime.parse("2025-01-10T23:59:59Z"))
            .build());
    }

    //쿠폰 발급 기능 API - 실패
    @PostMapping("/api/v1/coupons/issue/mock/error")
    public ResponseDTO<CouponIssueResponseDTO> issueCouponFail(@RequestBody CouponIssueRequestDTO requestDTO) {
        return ResponseDTO.fail("No Coupons Found", null);
    }

    //보유 쿠폰 조회 기능 API - 성공
    @GetMapping("/api/v1/coupons/{userId}/mock")
    public ResponseDTO<List<CouponResponseDTO>> getCouponsSuccess(@PathVariable long userId) {
        return ResponseDTO.success(List.of(
            CouponResponseDTO.builder()
                .couponId(5001L)
                .userId(userId)
                .couponCode("AAA")
                .discount(1000)
                .status(CouponStatus.ACTIVE)
                .issueDate(LocalDateTime.parse("2025-01-01T12:00:00Z"))
                .expireDate(LocalDateTime.parse("2025-01-10T23:59:59Z"))
                .build()
        ));
    }

    //보유 쿠폰 조회 기능 API - 실패
    @GetMapping("/api/v1/coupons/{userId}/mock/error")
    public ResponseDTO<List<CouponResponseDTO>> getCouponsFail(@PathVariable long userId) {
        return ResponseDTO.fail("No Coupons Found", null);
    }
}
