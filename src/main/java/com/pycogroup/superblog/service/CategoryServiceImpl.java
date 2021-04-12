package com.pycogroup.superblog.service;


import com.pycogroup.superblog.exception.AlreadyExistedException;
import com.pycogroup.superblog.exception.BadRequestException;
import com.pycogroup.superblog.exception.ResourceNotFoundException;
import com.pycogroup.superblog.model.Category;
import com.pycogroup.superblog.repository.ArticleRepository;
import com.pycogroup.superblog.repository.CategoryRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public Category createCategory(Category category) {
        String categoryName = category.getCategoryName().toLowerCase();
        List<String> categoryNames = new ArrayList<>();
        List<Category> categories = getAllCategories();
        if (categories != null) {
            categories.forEach(c -> {
                categoryNames.add(c.getCategoryName());
            });
            if (categoryNames.contains(categoryName)) {
                throw new AlreadyExistedException("categoryName-" + categoryName + " already existed");
            }
        }
        category.setCategoryName(categoryName);
        return categoryRepository.save(category);
    }

    @Override
    public void updateCategory(String categoryId, String newCategoryName) {
        if (newCategoryName == null) {
            throw new BadRequestException("categoryName can not null");
        }
        if (!isCategoryExists(categoryId)) {
            throw new ResourceNotFoundException("categoryId-" + categoryId);
        }
        categoryRepository.updateCategoryNameWithCategoryId(categoryId, newCategoryName);
        articleRepository.updateArticlesCategoryName(categoryId, newCategoryName);
    }

    @Override
    public void deleteCategoryByCategoryId(String categoryId) {
        if (!isCategoryExists(categoryId)) {
            throw new ResourceNotFoundException("categoryId-" + categoryId);
        }
        categoryRepository.deleteById(new ObjectId(categoryId));
        articleRepository.deleteArticleCategoryByCategoryId(categoryId);
    }

    private boolean isCategoryExists(String categoryId) {
        Optional<Category> optionalCategory = categoryRepository.findById(new ObjectId(categoryId));
        return optionalCategory.isPresent();
    }


}
