package kr.hhplus.be.server.service.cart;

import kr.hhplus.be.server.domain.cart.entity.CartItem;

import java.util.List;

public interface CartItemService {

    List<CartItem> getCartItemsByIds(List<Long> cartItemIds);

    void deleteCartItems(List<CartItem> cartItems);
}
