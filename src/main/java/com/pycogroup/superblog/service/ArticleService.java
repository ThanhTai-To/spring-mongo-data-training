package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Comment;

import java.util.List;

public interface ArticleService {
	List<Article> getAllArticles();
	Article createArticle(Article article);
	String createComment(String articleId, Comment comment);
	void updateArticle(String articleId, Article article);
	void updateComment(String articleId, String commentId, String updateCommentAuthorEmail,Comment comment);
	void deleteArticle(String articleId);


}
