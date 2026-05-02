package com.ecommerce.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Constants;
import com.ecommerce.dto.ProductQueryDTO;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<Category> getCategoryList() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getStatus, Constants.ProductStatus.ON_SALE)
                .orderByAsc(Category::getSort);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public List<Product> getProductList(ProductQueryDTO dto) {
        Page<Product> page = new Page<>(dto.getPage(), dto.getPageSize());
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Product::getStatus, Constants.ProductStatus.ON_SALE);
        if (dto.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, dto.getCategoryId());
        }
        if (StringUtils.hasText(dto.getKeyword())) {
            wrapper.and(w -> w.like(Product::getName, dto.getKeyword())
                    .or().like(Product::getSubtitle, dto.getKeyword()));
        }
        if ("price_asc".equals(dto.getSort())) {
            wrapper.orderByAsc(Product::getPrice);
        } else if ("price_desc".equals(dto.getSort())) {
            wrapper.orderByDesc(Product::getPrice);
        } else if ("sales".equals(dto.getSort())) {
            wrapper.orderByDesc(Product::getSales);
        } else {
            wrapper.orderByDesc(Product::getId);
        }
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    public Product getProductDetail(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null || product.getStatus().equals(Constants.ProductStatus.OFF_SALE)) {
            throw new BusinessException("Product not found");
        }
        return product;
    }
}
