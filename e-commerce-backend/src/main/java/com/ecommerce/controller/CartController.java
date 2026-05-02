package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.CartDTO;
import com.ecommerce.entity.Cart;
import com.ecommerce.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public Result<Void> addToCart(@RequestAttribute Long userId, @RequestBody CartDTO dto) {
        cartService.addToCart(userId, dto);
        return Result.success();
    }

    @PutMapping("/update/{id}")
    public Result<Void> updateCartQuantity(@RequestAttribute Long userId, @PathVariable Long id, @RequestParam Integer quantity) {
        cartService.updateCartQuantity(userId, id, quantity);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> removeFromCart(@RequestAttribute Long userId, @PathVariable Long id) {
        cartService.removeFromCart(userId, id);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<Cart>> getCartList(@RequestAttribute Long userId) {
        List<Cart> list = cartService.getCartList(userId);
        return Result.success(list);
    }

    @PutMapping("/selectAll")
    public Result<Void> selectAll(@RequestAttribute Long userId, @RequestParam Integer selected) {
        cartService.selectAll(userId, selected);
        return Result.success();
    }

    @PutMapping("/select/{id}")
    public Result<Void> selectItem(@RequestAttribute Long userId, @PathVariable Long id, @RequestParam Integer selected) {
        cartService.selectItem(userId, id, selected);
        return Result.success();
    }

    @GetMapping("/count")
    public Result<Integer> getCartCount(@RequestAttribute Long userId) {
        Integer count = cartService.getCartCount(userId);
        return Result.success(count);
    }
}
