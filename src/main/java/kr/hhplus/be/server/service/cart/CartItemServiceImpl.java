package kr.hhplus.be.server.service.cart;

import kr.hhplus.be.server.domain.cart.entity.CartItem;
import kr.hhplus.be.server.domain.cart.repository.CartItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;

    // 장바구니 가져오기
    @Override
    public List<CartItem> getCartItemsByIds(List<Long> cartItemIds) {
        return cartItemRepository.findByIdIn(cartItemIds);
    }

    @Override
    public void deleteCartItems(List<CartItem> cartItems) {
        cartItemRepository.deleteAll(cartItems);
    }
}
