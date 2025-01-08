package kr.hhplus.be.server.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.controller.order.application.OrderApplicationService;
import kr.hhplus.be.server.controller.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.controller.order.dto.OrderResponseDTO;
import kr.hhplus.be.server.service.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "주문 API", description = "주문/결제 API")
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderApplicationService orderApplicationService;

    // 주문
    @Operation(summary = "주문 결제", description = "사용자의 주문결제를 처리합니다.")
    @PostMapping("/api/v1/orders")
    public ResponseDTO<OrderResponseDTO> parOrder(@RequestBody OrderRequestDTO requestDTO) {

        OrderVO orderVO = orderApplicationService.payOrder(requestDTO.getCartItemIds(), requestDTO.getUserCouponId());
        OrderResponseDTO responseDTO = OrderResponseDTO.from(orderVO);
        return ResponseDTO.success(responseDTO);
    }
}
