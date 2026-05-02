package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Constants;
import com.ecommerce.dto.CartDTO;
import com.ecommerce.entity.Cart;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.CartMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.CartService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public void addToCart(Long userId, CartDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || product.getStatus().equals(Constants.ProductStatus.OFF_SALE)) {
            throw new BusinessException("Product not found");
        }
        if (product.getStock() < dto.getQuantity()) {
            throw new BusinessException("Insufficient stock");
        }
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).eq(Cart::getProductId, dto.getProductId());
        Cart existCart = cartMapper.selectOne(wrapper);
        if (existCart != null) {
            existCart.setQuantity(existCart.getQuantity() + dto.getQuantity());
            if (existCart.getQuantity() > product.getStock()) {
                throw new BusinessException("Insufficient stock");
            }
            cartMapper.updateById(existCart);
        } else {
            Cart cart = new Cart();
            BeanUtils.copyProperties(dto, cart);
            cart.setUserId(userId);
            cart.setSelected(Constants.CartSelected.NOT_SELECTED);
            cartMapper.insert(cart);
        }
    }

    @Override
    public void updateCartQuantity(Long userId, Long cartId, Integer quantity) {
        Cart cart = getCartByIdAndUserId(userId, cartId);
        Product product = productMapper.selectById(cart.getProductId());
        if (quantity > product.getStock()) {
            throw new BusinessException("Insufficient stock");
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    public void removeFromCart(Long userId, Long cartId) {
        Cart cart = getCartByIdAndUserId(userId, cartId);
        cartMapper.deleteById(cartId);
    }

    @Override
    public List<Cart> getCartList(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId).orderByDesc(Cart::getId);
        List<Cart> carts = cartMapper.selectList(wrapper);
        for (Cart cart : carts) {
            Product product = productMapper.selectById(cart.getProductId());
            cart.setProduct(product);
        }
        return carts;
    }

    @Override
    public void selectAll(Long userId, Integer selected) {
        LambdaUpdateWrapper<Cart> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Cart::getUserId, userId).set(Cart::getSelected, selected);
        cartMapper.update(null, wrapper);
    }

    @Override
    public void selectItem(Long userId, Long cartId, Integer selected) {
        Cart cart = getCartByIdAndUserId(userId, cartId);
        cart.setSelected(selected);
        cartMapper.updateById(cart);
    }

    @Override
    public Integer getCartCount(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        return cartMapper.selectCount(wrapper).intValue();
    }

    private Cart getCartByIdAndUserId(Long userId, Long cartId) {
        Cart cart = cartMapper.selectById(cartId);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException("Cart item not found");
        }
        return cart;
    }
}
