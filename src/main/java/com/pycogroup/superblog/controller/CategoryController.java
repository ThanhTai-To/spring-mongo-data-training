package com.pycogroup.superblog.controller;

import com.pycogroup.superblog.api.CategoriesApi;
import com.pycogroup.superblog.api.model.CategoryListResponse;
import com.pycogroup.superblog.api.model.CreateCategoryRequest;
import com.pycogroup.superblog.api.model.ObjectCreationSuccessResponse;
import com.pycogroup.superblog.api.model.UpdateCategoryRequest;
import com.pycogroup.superblog.exception.ResourceNotFoundException;
import com.pycogroup.superblog.model.Category;
import com.pycogroup.superblog.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class CategoryController implements CategoriesApi {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ResponseEntity<CategoryListResponse> getCategoryList() {
        List<Category> categoryList = categoryService.getAllCategories();
        return buildArticleListResponse(categoryList);
    }

    @Override
    public ResponseEntity<ObjectCreationSuccessResponse> createCategory(@Valid CreateCategoryRequest createCategoryRequest) {
        Category category = modelMapper.map(createCategoryRequest, Category.class);
        log.info("\nCategoryController: Start categoryService.createCategory()");
        Category persistCategory = categoryService.createCategory(category);
        log.info("\nCategoryController: End categoryService.createCategory()");
        ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
        result.setId(persistCategory.getCategoryId().toString());
        result.setResponseCode(HttpStatus.CREATED.value());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<ObjectCreationSuccessResponse> updateCategory(String categoryId, @Valid UpdateCategoryRequest updateCategoryRequest) {
        log.info("\nCategoryController: Start categoryService.updateCategory()");
        categoryService.updateCategory(categoryId, updateCategoryRequest.getCategoryName());
        log.info("\nCategoryController: End categoryService.updateCategory()");
        ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
        result.setId(categoryId);
        result.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ObjectCreationSuccessResponse> deleteCategory(String categoryId) {
        log.info("\nCategoryController: Start categoryService.deleteCategory()");
        categoryService.deleteCategoryByCategoryId(categoryId);
        log.info("\nCategoryController: End categoryService.deleteCategory()");
        ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
        result.setId(categoryId);
        result.setResponseCode(HttpStatus.OK.value());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private ResponseEntity<CategoryListResponse> buildArticleListResponse(List<Category> categoryList) {
        CategoryListResponse categoryListResponse = new CategoryListResponse();
        if (categoryList != null) {
            categoryList.forEach(item -> categoryListResponse.addCategoriesItem(modelMapper.map(item, com.pycogroup.superblog.api.model.CategoryResponseModel.class)));
        }
        return new ResponseEntity(categoryListResponse, HttpStatus.OK);
    }
}
