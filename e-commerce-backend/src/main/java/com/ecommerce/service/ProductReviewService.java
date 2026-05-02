package com.ecommerce.service;

import com.ecommerce.dto.ProductReviewCreateDTO;
import com.ecommerce.entity.ProductReview;

import java.util.Map;

public interface ProductReviewService {

    void createReview(Long userId, ProductReviewCreateDTO dto);

    Map<String, Object> listByProduct(Long productId, Integer page, Integer pageSize);
}
