package com.ecommerce.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ecommerce.common.BusinessException;
import com.ecommerce.common.Result;
import com.ecommerce.entity.Category;
import com.ecommerce.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryMapper categoryMapper;

    @GetMapping
    public Result<List<Category>> list() {
        LambdaQueryWrapper<Category> w = new LambdaQueryWrapper<>();
        w.orderByAsc(Category::getSort).orderByDesc(Category::getId);
        return Result.success(categoryMapper.selectList(w));
    }

    @GetMapping("/{id}")
    public Result<Category> detail(@PathVariable Long id) {
        Category c = categoryMapper.selectById(id);
        if (c == null) {
            throw new BusinessException("分类不存在");
        }
        return Result.success(c);
    }

    @PostMapping
    public Result<Void> create(@RequestBody Category category) {
        category.setId(null);
        categoryMapper.insert(category);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        categoryMapper.updateById(category);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.deleteById(id);
        return Result.success();
    }
}
