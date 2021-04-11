package com.pycogroup.superblog.controller;

import com.pycogroup.superblog.api.model.CategoryListResponse;
import com.pycogroup.superblog.api.model.CreateCategoryRequest;
import com.pycogroup.superblog.api.model.UpdateCategoryRequest;
import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Category;
import com.pycogroup.superblog.model.User;
import com.pycogroup.superblog.service.CategoryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.context.WebApplicationContext;

import java.util.Collections;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CategoryControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private CategoryController categoryController;

//    @Autowired
//    private WebApplicationContext webApplicationContext;
//
//    @Autowired
//    private CategoryService categoryService;

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
    public void testGetCategoryList()  {
        List<Category> categoryList = mongoTemplate.findAll(Category.class);
        given(categoryService.getAllCategories()).willReturn(categoryList);

//        mockMvc.perform(MockMvcRequestBuilders.get("/categories")
//                .accept(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(MockMvcResultMatchers.jsonPath("$.articles").exists());
        Assert.assertEquals(200, categoryController.getCategoryList().getStatusCodeValue());
    }

//    @Test
//    public void itShouldReturnCategoryList() throws Exception {
//        mongoTemplate.remove(Category.class).all();
//        Category category = mongoTemplate.save(Category.builder()
//                .categoryName("travel")
//                .build());
//        when(categoryService.getAllCategories()).thenReturn(Collections.singletonList(category));
//
//        mockMvc.perform(get("/categories")
//                .con
//                .expect(status().isOk())
//                .andExpect())
//    }

//    @Test
//    public void testCreateCategory() {
//        Category category = new Category();
//        category.setCategoryName("news");
//        Category createdCategory = categoryService.createCategory(category);
//        assert createdCategory != null;
//        Assert.assertEquals(category.getCategoryName(), createdCategory.getCategoryName());
//    }

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
