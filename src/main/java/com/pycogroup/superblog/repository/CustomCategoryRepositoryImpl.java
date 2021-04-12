package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomCategoryRepositoryImpl implements CustomCategoryRepository {

    @Autowired
    private MongoOperations mongoOperations;

    @Override
    public void updateCategoryNameWithCategoryId(String categoryId, String newCategoryName) {
        Query query = new Query(Criteria.where("categoryId").is(categoryId));
        Update update = new Update();
        update.set("categoryName", newCategoryName);
        mongoOperations.updateFirst(query, update, Category.class);
    }

    @Override
    public List<Category> findByCategoryNameIn(List<String> categoryNames) {
        Query query = new Query();
        query.addCriteria(Criteria.where("categoryName").in(categoryNames));
        return mongoOperations.find(query, Category.class);
    }
}
