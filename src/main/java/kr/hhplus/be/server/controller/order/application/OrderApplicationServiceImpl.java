package kr.hhplus.be.server.controller.order.application;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import kr.hhplus.be.server.client.DataPlatformClient;
import kr.hhplus.be.server.controller.exception.CommerceCouponException;
import kr.hhplus.be.server.controller.exception.CommerceOrderException;
import kr.hhplus.be.server.controller.exception.CommerceProductException;
import kr.hhplus.be.server.controller.exception.CommerceUserException;
import kr.hhplus.be.server.domain.balance.entity.BalanceHistory;
import kr.hhplus.be.server.domain.cart.entity.CartItem;
import kr.hhplus.be.server.domain.common.ErrorCode;
import kr.hhplus.be.server.domain.coupon.code.CouponStatus;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.entity.UserCoupon;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.payment.entity.Payment;
import kr.hhplus.be.server.domain.payment.entity.PaymentBalance;
import kr.hhplus.be.server.domain.payment.entity.PaymentCoupon;
import kr.hhplus.be.server.domain.product.code.ProductStatus;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.user.entity.User;
import kr.hhplus.be.server.service.balance.BalanceHistoryService;
import kr.hhplus.be.server.service.cart.CartItemService;
import kr.hhplus.be.server.service.coupon.CouponUsedHistoryService;
import kr.hhplus.be.server.service.coupon.UserCouponService;
import kr.hhplus.be.server.service.order.OrderItemService;
import kr.hhplus.be.server.service.order.OrderService;
import kr.hhplus.be.server.service.order.vo.OrderVO;
import kr.hhplus.be.server.service.payment.PaymentBalanceService;
import kr.hhplus.be.server.service.payment.PaymentCouponService;
import kr.hhplus.be.server.service.payment.PaymentService;
import kr.hhplus.be.server.service.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

@Service
@RequiredArgsConstructor
public class OrderApplicationServiceImpl implements OrderApplicationService {

    private final CartItemService cartItemService;
    private final OrderService orderService;
    private final OrderItemService orderItemService;
    private final UserCouponService userCouponService;
    private final CouponUsedHistoryService couponUsedHistoryService;
    private final BalanceHistoryService balanceHistoryService;
    private final PaymentService paymentService;
    private final PaymentBalanceService paymentBalanceService;
    private final PaymentCouponService paymentCouponService;
    private final DataPlatformClient dataClient;
    private final ProductService productService;

    @Override
    @Transactional
    public OrderVO payOrder(List<Long> cartItemIds, Long userCouponId) {
        List<CartItem> cartItems = validateAndFetchCartItems(cartItemIds);
        BigDecimal totalPrice = calculateTotalPrice(cartItems);
        User user = cartItems.get(0).getUser();

        validateProductStock(cartItems);

        Order order = createOrderWithItems(user, totalPrice, cartItems);
        BigDecimal couponDiscountPrice = applyCouponDiscount(userCouponId, user, totalPrice);

        processPayment(user, order, totalPrice, couponDiscountPrice, cartItems);
        order.updateSuccessPayment();

        productStockReduce(order.getOrderItems());

        //외부 api에 데이터 전송
        dataClient.sendData();

        return OrderVO.from(order);
    }

    private List<CartItem> validateAndFetchCartItems(List<Long> cartItemIds) {
        List<CartItem> cartItems = cartItemService.getCartItemsByIds(cartItemIds);

        if (cartItemIds.size() != cartItems.size()) {
            throw new CommerceOrderException(ErrorCode.CART_ITEM_COUNT_MISMATCH);
        }

        cartItems.forEach(this::validateCartItem);
        return cartItems;
    }

    private void validateCartItem(CartItem cartItem) {
        if (cartItem.getQuantity() <= 0) {
            throw new CommerceProductException(ErrorCode.INVALID_PRODUCT_QUANTITY);
        }

        Product product = cartItem.getProduct();
        if (ObjectUtils.isEmpty(product) || !ProductStatus.SALE.equals(product.getStatus())) {
            throw new CommerceProductException(ErrorCode.PRODUCT_INSUFFICIENT_INVENTORY);
        }
    }

    private void validateProductStock(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
      Product product =
          productService
              .getProductByProductId(cartItem.getProduct().getId())
              .orElseThrow(() -> new CommerceProductException(ErrorCode.PRODUCT_NOT_EXIST));
            if (product.getStock() < cartItem.getQuantity()) {
                throw new CommerceProductException(ErrorCode.PRODUCT_INSUFFICIENT_INVENTORY);
            }
        }
    }

    private BigDecimal calculateTotalPrice(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal applyCouponDiscount(Long userCouponId, User user, BigDecimal totalPrice) {
        if (userCouponId == null) {
            return BigDecimal.ZERO;
        }

        UserCoupon userCoupon = userCouponService.getUserCouponByCouponIdAndUserId(userCouponId, user.getId())
                .orElseThrow(() -> new CommerceCouponException(ErrorCode.COUPON_NOT_EXIST));

        validateUserCoupon(userCoupon);

        Coupon coupon = userCoupon.getCoupon();

        // 쿠폰 할인 예정 금액 = 결제 금액 * 할인율 / 100
        // 나누기 시 1의 자리에서 내림 처림
        return totalPrice.multiply(
                        BigDecimal.valueOf(coupon.getDiscount()))
                .divide(
                        BigDecimal.valueOf(100),
                        0,
                        RoundingMode.DOWN
                );
    }

    private void validateUserCoupon(UserCoupon userCoupon) {
        if (!CouponStatus.ACTIVE.equals(userCoupon.getStatus()) || LocalDateTime.now().isAfter(userCoupon.getExpiredAt())) {
            throw new CommerceCouponException(ErrorCode.COUPON_NOT_AVAILABLE);
        }
    }

    private Order createOrderWithItems(User user, BigDecimal totalPrice, List<CartItem> cartItems) {
        Order order = orderService.createOrder(user, totalPrice);
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> orderItemService.createOrderItem(order, cartItem.getProduct(), cartItem.getQuantity(), cartItem.getProduct().getPrice()))
                .toList();
        order.setOrderItems(orderItems);
        return order;
    }

    private void processPayment(User user, Order order, BigDecimal totalPrice, BigDecimal couponDiscountPrice, List<CartItem> cartItems) {
        BigDecimal needPayBalanceAmount = totalPrice.subtract(couponDiscountPrice);

        if (balanceHistoryService.calculate(user).compareTo(needPayBalanceAmount) < 0) {
            throw new CommerceUserException(ErrorCode.INSUFFICIENT_BALANCE);
        }

        BalanceHistory usedBalanceHistory = balanceHistoryService.use(user, needPayBalanceAmount);
        Payment payment = paymentService.save(order, totalPrice);

        if (couponDiscountPrice.compareTo(BigDecimal.ZERO) > 0) {
            UserCoupon usedCoupon = userCouponService.use(userCouponService.getUserCouponByCouponIdAndUserId(order.getUser().getId(), user.getId()).orElseThrow());
            couponUsedHistoryService.save(user.getId(), usedCoupon.getId());
            PaymentCoupon paymentCoupon = paymentCouponService.save(payment, usedCoupon, couponDiscountPrice);
            payment.setPaymentCoupon(paymentCoupon);
        }

        PaymentBalance paymentBalance = paymentBalanceService.save(payment, usedBalanceHistory, needPayBalanceAmount);
        payment.setPaymentBalance(paymentBalance);
        cartItemService.deleteCartItems(cartItems);

        order.setPayment(payment);
    }

    private void productStockReduce(List<OrderItem> orderItems) {
        orderItems.forEach(orderItem -> {
            Product product = orderItem.getProduct();
            product.reduceStock(orderItem.getQuantity());
        });
    }
}