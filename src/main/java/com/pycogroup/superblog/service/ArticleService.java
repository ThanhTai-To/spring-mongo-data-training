package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Comment;

import java.util.List;

public interface ArticleService {

	List<Article> getAllArticles();

	Article createArticle(Article articleRequest);

    void updateArticle(String articleId, Article article);

    void deleteArticle(String articleId);

    Comment createComment(String articleId, Comment comment);

    void updateComment(String articleId, String commentId, String userUpdateId, String status);

    void deleteComment(String articleId, String commentId);
}
