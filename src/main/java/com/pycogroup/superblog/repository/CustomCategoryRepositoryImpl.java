package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public CustomCategoryRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void updateCategoryNameWithCategoryId(String categoryId, String newCategoryName) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId));
        Update update = new Update();
        update.set("categoryName", newCategoryName);
        mongoTemplate.updateFirst(query, update, Category.class);
    }

    @Override
    public List<Category> findByCategoryNameIn(List<String> categoryNames) {
        Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").in(categoryNames));
        return mongoTemplate.find(query, Category.class);
    }
}
