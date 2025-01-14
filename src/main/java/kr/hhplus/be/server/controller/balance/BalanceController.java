package kr.hhplus.be.server.controller.balance;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.controller.balance.application.BalanceApplicationService;
import kr.hhplus.be.server.controller.balance.dto.BalanceChargeRequestDTO;
import kr.hhplus.be.server.controller.balance.dto.BalanceChargeResponseDTO;
import kr.hhplus.be.server.controller.balance.dto.BalanceResponseDTO;
import kr.hhplus.be.server.service.balance.vo.BalanceChargeVO;
import kr.hhplus.be.server.service.balance.vo.BalanceVO;
import kr.hhplus.be.server.service.user.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@Tag(name = "잔액 API", description = "잔액 조회 / 충전 API")
@RestController
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceApplicationService balanceApplication;

    //잔액 충전
    @Operation(summary = "잔액 충전", description = "사용자 ID로 잔액을 충전합니다.")
    @PostMapping("/api/v1/balance/charge/{userId}")
    public ResponseDTO<BalanceChargeResponseDTO> chargeBalance(@RequestBody BalanceChargeRequestDTO request) {

        BalanceChargeVO chargeVO = BalanceChargeVO.builder()
                .userId(request.getUserId())
                .amount(request.getAmount())
                .build();

        BalanceVO balanceVO = balanceApplication.chargeBalance(chargeVO);

        return ResponseDTO.success(BalanceChargeResponseDTO.builder()
                .userId(balanceVO.getUser().getId())
                .amount(balanceVO.getChangeAmount())
                .currentBalance(balanceVO.getCurrentBalance())
                .build()
        );
    }

    //잔액 조회
    @Operation(summary = "유저의 잔액 조회", description = "사용자 ID로 잔액을 조회합니다.")
    @GetMapping("/api/v1/balance/{userId}")
    public ResponseDTO<BalanceResponseDTO> getBalance(@PathVariable Long userId) {
        UserVO userVO = balanceApplication.getUser(userId);
        return ResponseDTO.success(userVO.toBalanceResponseDTO());
    }
}
