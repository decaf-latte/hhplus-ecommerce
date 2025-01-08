package kr.hhplus.be.server.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.order.repository.OrderItemRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.service.order.vo.TopOrderItemVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;

    @Override
    public OrderItem createOrderItem(Order order, Product product, int quantity, BigDecimal price) {
        OrderItem orderItem = OrderItem.of()
                .order(order)
                .product(product)
                .quantity(quantity)
                .price(price)
                .build();

        return orderItemRepository.save(orderItem);
    }

    public List<TopOrderItemVO> getTopOrderItems(){

        Pageable pageable = PageRequest.of(0, 5); // 5개의 데이터만 가져옴
        return orderItemRepository.findTopOrderItemsLast3Days(LocalDate.now().minusDays(3),pageable);

    }
}
