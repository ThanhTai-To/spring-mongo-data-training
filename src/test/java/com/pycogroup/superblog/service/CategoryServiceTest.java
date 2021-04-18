package com.pycogroup.superblog.service;

import com.pycogroup.superblog.exception.AlreadyExistedException;
import com.pycogroup.superblog.exception.BadRequestException;
import com.pycogroup.superblog.exception.ResourceNotFoundException;
import com.pycogroup.superblog.model.Category;

import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;


import java.util.List;




//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CategoryServiceTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private CategoryService categoryService;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    @Before
    public void init() {
        mongoTemplate.remove(Category.class);
        mongoTemplate.save(Category.builder()
                .categoryName("food")
                .build());
        mongoTemplate.save(Category.builder()
                .categoryName("weather")
                .build());
    }

    @Test
    public void testGetAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        assert categories != null;
    }

    @Test
    public void testCreateCategory() {
        Category category = Category.builder()
                .categoryName("travel")
                .build();
        Category createCategory = categoryService.createCategory(category);
        assert createCategory != null;
    }

    @Test
    public void testCreateCategoryAlreadyCreated() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("travel")
                .build());
        try {
            categoryService.createCategory(category);
        } catch (AlreadyExistedException ignored) {
        }
    }

    @Test
    public void testUpdateCategoryWithNullNewName() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("health")
                .build());
        try {
            categoryService.updateCategory(category.getCategoryId().toString(), null);
        } catch (BadRequestException ex) {
        }
    }

    @Test
    public void testUpdateCategoryWithCategoryIdNotFound() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("social")
                .build());
        try {
            categoryService.updateCategory("07b9e2d7f55842e9de45ce31", "security");
        } catch (ResourceNotFoundException ex) {
        }
    }

    @Test
    public void testUpdateCategory() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("social")
                .build());
        categoryService.updateCategory(category.getCategoryId().toString(), "engineer");
        Category updateCategory = mongoTemplate.findById(category.getCategoryId(), Category.class);
        assert updateCategory != null;
        Assertions.assertEquals("engineer", updateCategory.getCategoryName());
    }

    @Test
    public void testDeleteCategoryWithCategoryIdNotFound() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("social")
                .build());
        try {
            categoryService.deleteCategoryByCategoryId("07b9e2d7f55842e9de45ce31");
        } catch (ResourceNotFoundException ex) {
        }
    }

    @Test
    public void testDeleteCategoryByCategoryId() {
        Category category = mongoTemplate.save(Category.builder()
                .categoryName("social")
                .build());
        categoryService.deleteCategoryByCategoryId(category.getCategoryId().toString());
        Assertions.assertNull(mongoTemplate.findById(category.getCategoryId(), Category.class));

    }






}
