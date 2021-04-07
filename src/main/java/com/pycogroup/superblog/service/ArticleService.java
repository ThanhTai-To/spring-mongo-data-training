package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Comment;

import java.util.List;

public interface ArticleService {
	List<Article> getAllArticles();
	Article createArticle(Article article);
	String createComment(String articleId, Comment comment);
	void updateArticleByArticleId(String articleId, Article article);
	void updateCommentByCommentId(String articleId, String commentId, String updateCommentAuthorEmail,Comment comment);
	void deleteArticleByArticleBy(String articleId);
    void deleteCommentByCommentId(String articleId, String commentId);
}
