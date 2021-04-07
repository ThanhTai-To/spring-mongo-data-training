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
	public ResponseEntity<ObjectCreationSuccessResponse> createArticle(@Valid CreateArticleRequest createArticleRequest)  {
		Article article = modelMapper.map(createArticleRequest, Article.class);
		articleService.createArticle(article);
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(article.getArticleId());
		result.setResponseCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> createComment(String articleId, @Valid CreateCommentRequest createCommentRequest) {
		Comment comment =  modelMapper.map(createCommentRequest, Comment.class);
		String commentId = articleService.createComment(articleId, comment);
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(commentId);
		result.setResponseCode(HttpStatus.CREATED.value());
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	// Can only update article title, content, categories
	// Update any of above fields if it is not null
	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> updateArticle(String articleId, @Valid UpdateArticleRequest updateArticleRequest) {
		Article article = modelMapper.map(updateArticleRequest, Article.class);
		articleService.updateArticle(articleId, article);
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(articleId);
		result.setResponseCode(200);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	// Only allow article author can update status of comment
	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> updateComment(String articleId, String commentId, @Valid UpdateCommentRequest updateCommentRequest) {
		Comment comment = modelMapper.map(updateCommentRequest, Comment.class);
		articleService.updateComment(articleId, commentId, updateCommentRequest.getAuthorEmail() ,comment);
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		result.setId(commentId);
		result.setResponseCode(200);
		return new ResponseEntity<>(result, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<ObjectCreationSuccessResponse> deleteArticle(String articleId) {
		articleService.deleteArticle(articleId);
		ObjectCreationSuccessResponse result = new ObjectCreationSuccessResponse();
		// TODO
		result.setId(articleId);
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
