package kr.hhplus.be.server.service.order;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.service.order.vo.TopOrderItemVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class OrderItemServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemServiceImpl orderItemService;

    public OrderItemServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("정상적인 주문 항목 생성")
    void createOrderItem_Success() {
        Order order = Order.of().build();
        Product product = Product.of().build();
        int quantity = 1;
        BigDecimal price = BigDecimal.valueOf(1000);

        OrderItem orderItem = OrderItem.of()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(orderItem);

        OrderItem result = orderItemService.createOrderItem(order, product, quantity, price);

        assertNotNull(result);
    }

    @Test
    @DisplayName("최근 3일간 가장 많이 판매된 상품을 조회")
    void getTopOrderItems_Success() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(3);
        Pageable pageable = PageRequest.of(0, 5);

        TopOrderItemVO topOrderItem1 = new TopOrderItemVO(1L, 10L);
        TopOrderItemVO topOrderItem2 = new TopOrderItemVO(2L, 8L);
        List<TopOrderItemVO> mockTopOrderItems = List.of(topOrderItem1, topOrderItem2);

        when(orderItemRepository.findTopOrderItemsLast3Days(threeDaysAgo, pageable))
                .thenReturn(mockTopOrderItems);

        List<TopOrderItemVO> result = orderItemService.getTopOrderItems();

        assertNotNull(result);
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getProductId()).isEqualTo(1L);
        assertThat(result.get(0).getSalesCount()).isEqualTo(10L);
        assertThat(result.get(1).getProductId()).isEqualTo(2L);
        assertThat(result.get(1).getSalesCount()).isEqualTo(8L);
    }
}