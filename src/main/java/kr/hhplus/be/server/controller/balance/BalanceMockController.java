package kr.hhplus.be.server.controller.balance;

import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.controller.balance.dto.BalanceChargeRequestDTO;
import kr.hhplus.be.server.controller.balance.dto.BalanceChargeResponseDTO;
import kr.hhplus.be.server.controller.balance.dto.BalanceResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class BalanceMockController {

    //잔액 충전 API - 성공
    @PostMapping("/api/v1/balance/charge/{userId}/mock")
    public ResponseDTO<BalanceChargeResponseDTO> chargeBalanceSuccess(@RequestBody BalanceChargeRequestDTO request) {
    return ResponseDTO.success(BalanceChargeResponseDTO.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .currentBalance(20000)
                .build());
    }

    //잔액 충전 API - 실패
    @PostMapping("/api/v1/balance/charge/{userId}/mock/error")
    public ResponseDTO<BalanceChargeResponseDTO> chargeBalanceFail(@RequestBody BalanceChargeRequestDTO request) {
        return ResponseDTO.fail("User Not Found",null);
    }

    //잔액 조회 API - 성공
    @GetMapping("/api/v1/balance/{userId}/mock")
    public ResponseDTO<BalanceResponseDTO> getBalanceSuccess(@PathVariable long userId) {
        return ResponseDTO.success(BalanceResponseDTO.builder()
                .userId(userId)
                .currentBalance(15000)
                .build());
    }

    //잔액 조회 API - 실패
    @GetMapping("/api/v1/balance/{userId}/mock/error")
    public ResponseDTO<BalanceResponseDTO> getBalanceFail(@PathVariable long userId) {
        return ResponseDTO.fail("User Not Found",null);
    }

}
