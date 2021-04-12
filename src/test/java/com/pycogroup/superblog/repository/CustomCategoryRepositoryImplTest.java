package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Category;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomCategoryRepositoryImplTest {
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private CustomCategoryRepositoryImpl customCategoryRepositoryImpl;

    private Category initACategory() {
        mongoOperations.remove(Category.class).all();
        return mongoOperations.save(Category.builder()
                .categoryName(RandomStringUtils.random(15))
                .build());
    }

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
        List<Category> categoryListActual = customCategoryRepositoryImpl.findByCategoryNameIn(categoryNames);
        categoryListActual.forEach(categoryInList -> {
            Assert.assertEquals(category.getCategoryName(),categoryInList.getCategoryName());
            Assert.assertTrue(categoryNames.contains(categoryInList.getCategoryName()));
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
}
