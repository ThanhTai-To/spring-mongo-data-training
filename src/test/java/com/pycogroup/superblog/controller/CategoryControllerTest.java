package com.pycogroup.superblog.controller;

import com.pycogroup.superblog.api.model.CategoryListResponse;
import com.pycogroup.superblog.api.model.CreateCategoryRequest;
import com.pycogroup.superblog.api.model.UpdateCategoryRequest;
import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Category;
import com.pycogroup.superblog.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CategoryControllerTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryController categoryController;

    @Before
    public void setUp() {
        mongoTemplate.remove(Category.class).all();
        mongoTemplate.remove(Article.class).all();
        mongoTemplate.save(Category.builder()
                .categoryName("travel")
                .build());
        mongoTemplate.save(Category.builder()
                .categoryName("food")
                .build());
    }

    @Test
    public void testGetCategoryList() {
        Assert.assertEquals(200, categoryController.getCategoryList().getStatusCodeValue());
    }

    @Test
    public void testCreateCategory() {
        CreateCategoryRequest createCategoryRequest = new CreateCategoryRequest();
        createCategoryRequest.setCategoryName("education");
        int statusCode = categoryController.createCategory(createCategoryRequest).getStatusCodeValue();
        Assert.assertEquals(201, statusCode);
    }

    @Test
    public void testUpdateCategory() {
        UpdateCategoryRequest updateCategoryRequest = new UpdateCategoryRequest();
        updateCategoryRequest.setCategoryName("entertainment");
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("hot news")
                .build());
        Assert.assertEquals(200,categoryController.updateCategory(category.getCategoryId().toString(), updateCategoryRequest).getStatusCodeValue());
    }

    @Test
    public void testDeleteCategory() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("economic")
                .build());
        Assert.assertEquals(200,categoryController.deleteCategory(category.getCategoryId().toString()).getStatusCodeValue());
    }


}
