package kr.hhplus.be.server.controller.order.dto;

import kr.hhplus.be.server.domain.order.code.OrderStatus;
import kr.hhplus.be.server.domain.product.code.ProductStatus;
import kr.hhplus.be.server.service.order.vo.OrderItemVO;
import kr.hhplus.be.server.service.order.vo.OrderVO;
import kr.hhplus.be.server.service.payment.vo.PaymentVO;
import kr.hhplus.be.server.service.product.vo.ProductVO;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.ObjectUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderResponseDTO {

    private Long orderId;
    private Long userId;
    private BigDecimal totalPrice;
    private OrderStatus status;
    private List<OrderItemResponseData> orderItems;
    private OrderPaymentResponseData payment;

    @Builder
    public OrderResponseDTO(Long orderId, Long userId, BigDecimal totalPrice, OrderStatus status,
                            List<OrderItemResponseData> orderItems, OrderPaymentResponseData payment) {
        this.orderId = orderId;
        this.userId = userId;
        this.totalPrice = totalPrice;
        this.status = status;
        this.orderItems = orderItems;
        this.payment = payment;
    }

    public static OrderResponseDTO from(OrderVO orderVO) {

        if (ObjectUtils.isEmpty(orderVO)) {
            return null;
        }

        List<OrderItemResponseData> orderItemResponseDataList = orderVO.getOrderItems().stream()
                .map(OrderItemResponseData::from)
                .toList();

        return OrderResponseDTO.builder()
                .orderId(orderVO.getId())
                .userId(orderVO.getUser().getId())
                .totalPrice(orderVO.getTotalPrice())
                .status(orderVO.getStatus())
                .orderItems(orderItemResponseDataList)
                .payment(OrderPaymentResponseData.from(orderVO.getPayment()))
                .build();
    }

    @Getter
    @NoArgsConstructor
    public static class OrderItemResponseData {
        private Long orderItemId;
        private Long productId;
        private String productName;
        private BigDecimal productPrice;
        private ProductStatus productStatus;
        private int quantity;
        private BigDecimal orderItemPrice;

        @Builder
        public OrderItemResponseData(Long orderItemId, Long productId, String productName, BigDecimal productPrice, ProductStatus productStatus, int quantity, BigDecimal orderItemPrice) {
            this.orderItemId = orderItemId;
            this.productId = productId;
            this.productName = productName;
            this.productPrice = productPrice;
            this.productStatus = productStatus;
            this.quantity = quantity;
            this.orderItemPrice = orderItemPrice;
        }

        public static OrderItemResponseData from(OrderItemVO orderItemVO) {

            if (ObjectUtils.isEmpty(orderItemVO)) {
                return null;
            }

            ProductVO productVO = orderItemVO.getProduct();
            return OrderItemResponseData.builder()
                    .orderItemId(orderItemVO.getId())
                    .productId(productVO.getId())
                    .productName(productVO.getName())
                    .productPrice(productVO.getPrice())
                    .productStatus(productVO.getStatus())
                    .quantity(orderItemVO.getQuantity())
                    .orderItemPrice(orderItemVO.getPrice())
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class OrderPaymentResponseData {

        private Long paymentId;
        private BigDecimal totalAmount;
        private BigDecimal couponDiscountAmount;
        private BigDecimal balanceAmount;

        @Builder
        public OrderPaymentResponseData(Long paymentId, BigDecimal totalAmount, BigDecimal couponDiscountAmount, BigDecimal balanceAmount) {
            this.paymentId = paymentId;
            this.totalAmount = totalAmount;
            this.couponDiscountAmount = couponDiscountAmount;
            this.balanceAmount = balanceAmount;
        }

        public static OrderPaymentResponseData from(PaymentVO paymentVO) {

            if (ObjectUtils.isEmpty(paymentVO)) {
                return null;
            }

            BigDecimal couponDiscountAmount = ObjectUtils.isEmpty(paymentVO.getPaymentCoupon()) ? BigDecimal.ZERO : paymentVO.getPaymentCoupon().getAmount();
            BigDecimal balanceAmount = ObjectUtils.isEmpty(paymentVO.getPaymentBalance()) ? BigDecimal.ZERO : paymentVO.getPaymentBalance().getAmount();

            return OrderPaymentResponseData.builder()
                    .paymentId(paymentVO.getId())
                    .totalAmount(paymentVO.getAmount())
                    .couponDiscountAmount(couponDiscountAmount)
                    .balanceAmount(balanceAmount)
                    .build();
        }
    }
}
