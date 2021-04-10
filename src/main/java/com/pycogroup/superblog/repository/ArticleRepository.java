package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Article;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, ObjectId>,
        CustomArticleRepository,
        QuerydslPredicateExecutor<Article> {

    Article findByArticleId(ObjectId articleId);
    Optional<Article> findArticleByTitle(String title);
}
