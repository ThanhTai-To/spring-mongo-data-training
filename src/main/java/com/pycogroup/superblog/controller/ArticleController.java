package com.pycogroup.superblog.controller;

import com.pycogroup.superblog.api.ArticlesApi;
import com.pycogroup.superblog.api.model.*;
import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Comment;
import com.pycogroup.superblog.service.ArticleService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class ArticleController implements ArticlesApi {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ModelMapper modelMapper;

	@Override
	public ResponseEntity<ArticleListResponse> getArticleList() {
		List<Article> articleList = articleService.getAllArticles();
		return buildArticleListResponse(articleList);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> createArticle(@Valid CreateArticleRequest createArticleRequest) {
		log.info("\nArticleController: Start map article class");
		Article article = modelMapper.map(createArticleRequest, Article.class);
		log.info("\nArticleController: End mapping\nStart articleService.createArticle()");
		Article persistArticle = articleService.createArticle(article);
		log.info("\nArticleController: End articleService.createArticle()");
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(persistArticle.getArticleId().toString());
		result.setResponseCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> updateArticle(String articleId, @Valid UpdateArticleRequest updateArticleRequest) {
		Article article = modelMapper.map(updateArticleRequest, Article.class);
		log.info("\nArticleController: Start articleService.updateArticle()");
		articleService.updateArticle(articleId, article);
		log.info("\nArticleController: End articleService.updateArticle()");
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(articleId);
		result.setResponseCode(200);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> deleteArticle(String articleId) {
		log.info("\nArticleController: Start articleService.deleteArticle()");
		articleService.deleteArticle(articleId);
		log.info("\nArticleController: End articleService.deleteArticle()");
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(articleId);
		result.setResponseCode(HttpStatus.OK.value());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> createComment(String articleId, @Valid CreateCommentRequest createCommentRequest) {
		log.info("\nArticleController: Start mapping");
		Comment comment =  modelMapper.map(createCommentRequest, Comment.class);
		log.info("\nArticleController: Start articleService.createComment()");
		Comment persistComment = articleService.createComment(articleId, comment);
		log.info("\nArticleController: End articleService.createComment()");
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(persistComment.getCommentId());
		result.setResponseCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> updateComment(String articleId, String commentId, @Valid UpdateCommentRequest updateCommentRequest) {
		log.info("\nArticleController: Start articleService.updateComment()");
		articleService.updateComment(articleId, commentId, updateCommentRequest.getUserUpdateId(), updateCommentRequest.getStatus());
		log.info("\nArticleController: End articleService.updateComment()");
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(commentId);
		result.setResponseCode(200);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> deleteComment(String articleId, String commentId) {
		log.info("\nArticleController: Start articleService.deleteComment()");
		articleService.deleteComment(articleId, commentId);
		log.info("\nArticleController: Start articleService.deleteComment()");
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(commentId);
		result.setResponseCode(HttpStatus.OK.value());
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	private ResponseEntity<ArticleListResponse> buildArticleListResponse(List<Article> articleList) {
		ArticleListResponse articleListResponse = new ArticleListResponse();
		if (articleList != null) {
			articleList.forEach(item -> articleListResponse.addArticlesItem(modelMapper.map(item, com.pycogroup.superblog.api.model.ArticleResponseModel.class)));
		}
		return new ResponseEntity(articleListResponse, HttpStatus.OK);
	}
}
