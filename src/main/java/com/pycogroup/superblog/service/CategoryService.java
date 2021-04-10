package com.pycogroup.superblog.service;


import com.pycogroup.superblog.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategories();
    Category createCategory(Category category);
    void updateCategory(String categoryId, String newCategoryName);
    void deleteCategoryByCategoryId(String categoryId);
}
