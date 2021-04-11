package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Category;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import javax.validation.constraints.AssertTrue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomCategoryRepositoryImplTest {
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private CustomCategoryRepositoryImpl customCategoryRepositoryImpl;

    @Test
    public void testUpdateCategoryNameWithCorrectCategoryId() {
        Category category = initACategory();
        customCategoryRepositoryImpl.updateCategoryNameWithCategoryId(category.getCategoryId().toString(), "travel");
        Category updatedCategory = mongoOperations.findById(category.getCategoryId(), Category.class);
        assert updatedCategory != null;
        Assert.assertEquals("travel", updatedCategory.getCategoryName());
        Assert.assertEquals("travel", updatedCategory.getCategoryName());
        Assert.assertNotEquals("food",updatedCategory.getCategoryName());
        Assert.assertNotEquals(category.getCategoryName(),updatedCategory.getCategoryName());
    }

    @Test
    public void testFindByCategoryNameInListCategoryName() {
        Category category = initACategory();
        String[] categoryNamesArray = {"food", "travel", "education", category.getCategoryName()};
        List<String> categoryNames = Arrays.asList(categoryNamesArray);
        List<Category> categoryList = customCategoryRepositoryImpl.findByCategoryNameIn(categoryNames);
        categoryList.forEach(categoryInList -> {
            Assert.assertEquals(category.getCategoryName(), categoryInList.getCategoryName());
        });
    }

    @Test
    public void testFindByCategoryNameNotInListCategoryName() {
        Category category = initACategory();
        String[] categoryNamesArray = {"food", "travel", "education"};
        List<String> categoryNames = Arrays.asList(categoryNamesArray);
        List<Category> categoryList = customCategoryRepositoryImpl.findByCategoryNameIn(categoryNames);
        if (categoryList == null) {
            throw new AssertionError();
        }
    }

    @Test
    public void testQuery() {
        Category category = initACategory();
        String[] categoryNamesArray = {"food", "travel", "education", category.getCategoryName()};
        List<String> categoryNames = Arrays.asList(categoryNamesArray);
        Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").in(categoryNames));
        List<Category> categoryListExpected = mongoOperations.find(query, Category.class);
        List<Category> categoryListActual = customCategoryRepositoryImpl.findByCategoryNameIn(categoryNames);

        categoryListExpected.forEach(c -> {
            Assert.assertTrue(categoryNames.contains(c.getCategoryName()));
        });
    }

    private Category initACategory() {
        mongoOperations.remove(Category.class).all();
        return mongoOperations.save(Category.builder()
                .categoryName(RandomStringUtils.random(15))
                .build());
    }


}
