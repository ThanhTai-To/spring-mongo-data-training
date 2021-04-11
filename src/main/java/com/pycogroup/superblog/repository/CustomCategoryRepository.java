package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface CustomCategoryRepository {
    void updateCategoryNameWithCategoryId(String articleId, String newCategoryName);
    List<Category> findByCategoryNameIn(List<String> categoryNames);
}
