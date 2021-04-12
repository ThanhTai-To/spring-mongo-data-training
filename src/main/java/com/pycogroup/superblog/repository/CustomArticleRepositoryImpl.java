package com.pycogroup.superblog.repository;

import com.mongodb.BasicDBObject;
import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;
import java.util.stream.Collectors;

public class CustomArticleRepositoryImpl implements CustomArticleRepository {

    @Autowired
    MongoOperations mongoOperations;

    @Override
    public void updateArticlesCategoryName(String categoryId, String newCategoryName) {
        Query query = findCategoryByCategoryId(categoryId);
        Update update = new Update().set("categories.$.categoryName", newCategoryName);
        mongoOperations.updateMulti(query, update, Article.class);
    }

    @Override
    public List<String> getListCategoryName() {
        Query query = new Query();
        query.fields().include("categoryName").exclude("categoryId");
        return mongoOperations.find(query, Category.class)
                .stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteArticleCategoryByCategoryId(String categoryId) {
        Query query = findCategoryByCategoryId(categoryId);
        Update update = new Update().pull("categories", new BasicDBObject("categoryId", categoryId));
        mongoOperations.updateMulti(query, update, Article.class);
    }

    @Override
    public void updateCommentStatusByCommentId(String articleId, String commentId, String status) {
        Query query = queryWithArticleIdAndCommentId(articleId, commentId);
        Update update = new Update();
        update.set("comments.$.status", status);
        mongoOperations.updateFirst(query, update, Article.class);
    }

    @Override
    public void deleteCommentByCommentId(String articleId, String commentId) {
        Query query = queryWithArticleIdAndCommentId(articleId, commentId);
        Update update = new Update().pull("comments", new BasicDBObject("commentId", commentId));
        mongoOperations.updateFirst(query, update, Article.class);
    }

    private Query queryWithArticleIdAndCommentId(String articleId, String commentId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("articleId").is(articleId));
        query.addCriteria(Criteria.where("comments.commentId").is(commentId));
        return query;
    }

    public Query findCategoryByCategoryId(String categoryId) {
        return new Query(Criteria.where("categories.categoryId").is(categoryId));
    }

}
