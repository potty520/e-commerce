package com.ecommerce.service;

import com.ecommerce.dto.CartDTO;
import com.ecommerce.entity.Cart;
import java.util.List;

public interface CartService {
    void addToCart(Long userId, CartDTO dto);
    void updateCartQuantity(Long userId, Long cartId, Integer quantity);
    void removeFromCart(Long userId, Long cartId);
    List<Cart> getCartList(Long userId);
    void selectAll(Long userId, Integer selected);
    void selectItem(Long userId, Long cartId, Integer selected);
    Integer getCartCount(Long userId);
}
