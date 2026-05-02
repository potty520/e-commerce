package com.ecommerce.service;

import com.ecommerce.dto.ProductQueryDTO;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;
import java.util.List;

public interface ProductService {
    List<Category> getCategoryList();
    List<Product> getProductList(ProductQueryDTO dto);
    Product getProductDetail(Long productId);
}
