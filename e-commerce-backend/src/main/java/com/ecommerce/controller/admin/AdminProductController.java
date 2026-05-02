package com.ecommerce.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Result;
import com.ecommerce.entity.Product;
import com.ecommerce.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

    @Autowired
    private ProductMapper productMapper;

    @GetMapping
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        int p = page == null || page < 1 ? 1 : page;
        int ps = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);

        LambdaQueryWrapper<Product> cw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            cw.and(w -> w.like(Product::getName, keyword).or().like(Product::getSubtitle, keyword));
        }
        cw.orderByDesc(Product::getId);
        long total = productMapper.selectCount(cw);

        LambdaQueryWrapper<Product> lw = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            lw.and(w -> w.like(Product::getName, keyword).or().like(Product::getSubtitle, keyword));
        }
        lw.orderByDesc(Product::getId);
        lw.last("LIMIT " + (p - 1) * ps + "," + ps);
        List<Product> records = productMapper.selectList(lw);

        Map<String, Object> map = new HashMap<>();
        map.put("list", records);
        map.put("total", total);
        map.put("page", (long) p);
        map.put("pageSize", (long) ps);
        return Result.success(map);
    }

    @GetMapping("/{id}")
    public Result<Product> detail(@PathVariable Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new BusinessException("商品不存在");
        }
        return Result.success(product);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Product product) {
        product.setId(null);
        productMapper.insert(product);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        productMapper.updateById(product);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productMapper.deleteById(id);
        return Result.success();
    }
}
