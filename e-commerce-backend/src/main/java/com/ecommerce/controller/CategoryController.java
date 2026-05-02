package com.ecommerce.controller;

import com.ecommerce.common.Result;
import com.ecommerce.entity.Category;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
public class CategoryController {

    @Autowired
    private ProductService productService;

    @GetMapping("/list")
    public Result<List<Category>> getCategoryList() {
        List<Category> list = productService.getCategoryList();
        return Result.success(list);
    }
}
