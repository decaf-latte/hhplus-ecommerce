package kr.hhplus.be.server.service.order;

import java.math.BigDecimal;
import java.util.List;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderItem;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.service.order.vo.TopOrderItemVO;

public interface OrderItemService {

    OrderItem createOrderItem(Order order, Product product, int quantity, BigDecimal price);

    List<TopOrderItemVO> getTopOrderItems ();
}
