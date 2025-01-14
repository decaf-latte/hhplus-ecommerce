package kr.hhplus.be.server.controller.coupon;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.config.dto.ResponsePageDTO;
import kr.hhplus.be.server.controller.coupon.application.CouponApplicationService;
import kr.hhplus.be.server.controller.coupon.dto.CouponIssueRequestDTO;
import kr.hhplus.be.server.controller.coupon.dto.CouponResponseDTO;
import kr.hhplus.be.server.service.coupon.vo.UserCouponVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Tag(name = "쿠폰 API", description = "쿠폰 발급 / 보유 쿠폰 조회 API")
@RestController
@RequiredArgsConstructor
public class CouponController {

    private final CouponApplicationService couponApplicationService;

    // 쿠폰 발급 기능 API
    @Operation(summary = "쿠폰 발급", description = "사용자에게 쿠폰을 발급합니다.")
    @PostMapping("/api/v1/coupons/issue")
    public ResponseDTO<String> issueCouponSuccess(@RequestBody CouponIssueRequestDTO requestDTO) {

        couponApplicationService.issueCouponByCode(requestDTO.getUserId(), requestDTO.getCouponCode());

        return ResponseDTO.success("SUCCESS");
    }

    // 보유 쿠폰 목록 조회
    @Operation(summary = "보유 쿠폰 조회", description = "해당 사용자가 보유한 쿠폰을 조회합니다.")
    @GetMapping("/api/v1/coupons/{userId}")
    public ResponsePageDTO<CouponResponseDTO> getUserCoupons(@PathVariable Long userId, Pageable pageable) {
        List<UserCouponVO> userCouponVOS = couponApplicationService.getUserCoupons(userId);

        CouponResponseDTO responseDTO
                = new CouponResponseDTO(
                userCouponVOS.stream().map(CouponResponseDTO.UserCouponResponseData::from).toList()
        );

        return ResponsePageDTO.success(
                responseDTO,
                pageable.getPageNumber(),
                responseDTO.getUserCoupons().size()
        );
    }
}
