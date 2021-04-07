package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import java.util.Optional;

public interface ArticleRepository extends MongoRepository<Article, ObjectId>, QuerydslPredicateExecutor<Article> {

    Article findArticleByArticleId(String articleId);

    Optional<Article> findArticleByTitle(String title);

    //maybe need to add , fields = "{ 'comments.commentId' : 1 }"
    @Query(value = "{ 'articleId' : ?0, 'comments.commentId' : ?1 }")
    Optional<Article> findByArticleIdAndCommentsCommentId(String articleId, String commentId);

    void deleteByArticleId(String articleId);

//    @Query(value = "{ 'articleId' : ?0, 'comments.commentId' : ?1 }", fields = "{ 'comments.commentId' : 1 }", delete = true)
//    void deleteByArticleIdAndCommentsCommentId(String articleId, String commentId);
}
