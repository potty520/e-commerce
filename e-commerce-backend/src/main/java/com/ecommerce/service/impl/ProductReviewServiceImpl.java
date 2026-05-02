package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Constants;
import com.ecommerce.dto.ProductReviewCreateDTO;
import com.ecommerce.entity.Order;
import com.ecommerce.entity.OrderItem;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ProductReview;
import com.ecommerce.entity.User;
import com.ecommerce.mapper.OrderItemMapper;
import com.ecommerce.mapper.OrderMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.mapper.ProductReviewMapper;
import com.ecommerce.mapper.UserMapper;
import com.ecommerce.service.ProductReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ProductReviewServiceImpl implements ProductReviewService {

    @Autowired
    private ProductReviewMapper productReviewMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Override
    public void createReview(Long userId, ProductReviewCreateDTO dto) {
        Product product = productMapper.selectById(dto.getProductId());
        if (product == null || product.getStatus().equals(Constants.ProductStatus.OFF_SALE)) {
            throw new BusinessException("商品不存在或已下架");
        }
        LambdaQueryWrapper<ProductReview> dup = new LambdaQueryWrapper<>();
        dup.eq(ProductReview::getUserId, userId).eq(ProductReview::getProductId, dto.getProductId());
        if (productReviewMapper.selectCount(dup) > 0) {
            throw new BusinessException("您已评价过该商品");
        }
        if (dto.getOrderId() != null) {
            Order order = orderMapper.selectById(dto.getOrderId());
            if (order == null || !order.getUserId().equals(userId)) {
                throw new BusinessException("订单不存在");
            }
            LambdaQueryWrapper<OrderItem> oi = new LambdaQueryWrapper<>();
            oi.eq(OrderItem::getOrderId, dto.getOrderId()).eq(OrderItem::getProductId, dto.getProductId());
            if (orderItemMapper.selectCount(oi) == 0) {
                throw new BusinessException("该订单中未包含此商品");
            }
        }
        ProductReview r = new ProductReview();
        r.setUserId(userId);
        r.setProductId(dto.getProductId());
        r.setOrderId(dto.getOrderId());
        r.setRating(dto.getRating());
        r.setContent(StringUtils.hasText(dto.getContent()) ? dto.getContent().trim() : "");
        productReviewMapper.insert(r);
    }

    @Override
    public Map<String, Object> listByProduct(Long productId, Integer page, Integer pageSize) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 50);
        LambdaQueryWrapper<ProductReview> w = new LambdaQueryWrapper<>();
        w.eq(ProductReview::getProductId, productId).orderByDesc(ProductReview::getId);
        Page<ProductReview> pg = new Page<>(p, ps);
        productReviewMapper.selectPage(pg, w);
        List<ProductReview> list = pg.getRecords();
        for (ProductReview r : list) {
            User u = userMapper.selectById(r.getUserId());
            r.setNickname(u != null && StringUtils.hasText(u.getNickname()) ? u.getNickname() : (u != null ? u.getUsername() : "用户"));
        }
        Map<String, Object> map = new HashMap<>();
        map.put("list", list);
        map.put("total", pg.getTotal());
        map.put("page", (long) p);
        map.put("pageSize", (long) ps);
        return map;
    }
}
