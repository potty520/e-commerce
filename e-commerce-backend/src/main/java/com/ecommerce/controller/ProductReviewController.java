package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.dto.ProductReviewCreateDTO;
import com.ecommerce.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/product/review")
public class ProductReviewController {

    @Autowired
    private ProductReviewService productReviewService;

    /** 无需登录：商品详情展示评价列表 */
    @GetMapping("/list/{productId}")
    public Result<Map<String, Object>> list(@PathVariable Long productId,
                                            @RequestParam(required = false) Integer page,
                                            @RequestParam(required = false) Integer pageSize) {
        return Result.success(productReviewService.listByProduct(productId, page, pageSize));
    }

    @PostMapping
    public Result<Void> create(@RequestAttribute("userId") Long userId,
                               @Valid @RequestBody ProductReviewCreateDTO dto) {
        productReviewService.createReview(userId, dto);
        return Result.success();
    }
}
