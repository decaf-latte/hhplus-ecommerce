package kr.hhplus.be.server.controller.order;

import kr.hhplus.be.server.config.dto.ResponseDTO;
import kr.hhplus.be.server.controller.order.dto.OrderRequestDTO;
import kr.hhplus.be.server.controller.order.dto.OrderResponseDTO;
import kr.hhplus.be.server.domain.order.code.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class OrderMockController {

    //주문 API - 성공
    @PostMapping("/api/v1/orders/mock")
    public ResponseDTO<OrderResponseDTO> createOrderSuccess (@RequestBody OrderRequestDTO requestDTO) {
    return ResponseDTO.success(
        OrderResponseDTO.builder()
            .orderId(1001L)
            .userId(requestDTO.getUserId())
            .price(1000)
            .discount(1050)
            .finalPrice(1050)
            .remainingBalance(12950)
            .status(OrderStatus.COMPLETED)
            .orderDate(LocalDateTime.parse("2025-01-01T12:00:00Z"))
            .build());
    }

    //주문 API - 실패
    @PostMapping("/api/v1/orders/mock/error")
    public ResponseDTO<OrderResponseDTO> createOrderFail (@RequestBody OrderRequestDTO requestDTO) {
        return ResponseDTO.fail("Insufficient Balance or Stock",null);
    }

}
