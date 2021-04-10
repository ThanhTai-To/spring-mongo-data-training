package com.pycogroup.superblog.service;

import com.pycogroup.superblog.exception.AccessDeniedException;
import com.pycogroup.superblog.exception.AlreadyExistedException;
import com.pycogroup.superblog.exception.BadRequestException;
import com.pycogroup.superblog.exception.ResourceNotFoundException;
import com.pycogroup.superblog.model.*;
import com.pycogroup.superblog.repository.ArticleRepository;
import com.pycogroup.superblog.repository.CategoryRepository;
import com.pycogroup.superblog.repository.CommentRepository;
import com.pycogroup.superblog.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ArticleServiceImpl implements ArticleService {

	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private CommentRepository commentRepository;

	private final  String stringAuthorId = "authorId-";
	private final  String stringArticleId = "articleId-";

	@Override
	public List<Article> getAllArticles() {
		return articleRepository.findAll();
	}

	@Override
	public Article createArticle(Article article) {
		// Check if authorId exist in db
		User articleAuthor = userRepository.findUserById(article.getAuthorId());
		if (articleAuthor == null) {
			throw new ResourceNotFoundException(stringAuthorId + article.getAuthorId());
		}
		// Check if article title already existed
		if (isArticleTitleAlreadyExisted(article.getTitle())) {
			throw new AlreadyExistedException("articleTitle-" + article.getTitle());
		}
		// Check categories null
		if (article.getCategories() == null) {
			throw new BadRequestException("categories can not null");
		}
		// category name must be lower case and distinct in one article
		List<String> categoryNames = categoryFilter(article.getCategories());
		// TODO: need to improve: throw which categories don't exist
		// check if these category name exist in db
		if (!isCategoryListExist(categoryNames)){
			throw new ResourceNotFoundException("categories-" + categoryNames);
		}

		// find all categories from categories table that matched category name
		List<Category> categoryList = categoryRepository.findByCategoryNameIn(categoryNames);
		article.setCategories(categoryList);
		article.setAuthorName(articleAuthor.getName());
		LocalDateTime createdAt = LocalDateTime.now();
		article.setCreatedAt(createdAt);

		return articleRepository.save(article);
	}

	// Only article author can update
	// and he/she can only update title, content, categories
	@Override
	public void updateArticle(String articleId, Article updateArticle) {
		// check if userId who made modify null or not
		if (updateArticle.getAuthorId() == null) {
			throw new BadRequestException("authorId can not null");
		}
		// check if authorId in db
		Optional<User> optionalUser = userRepository.findById(updateArticle.getAuthorId());
		if (optionalUser.isEmpty()) {
			throw new ResourceNotFoundException(stringAuthorId + updateArticle.getAuthorId());
		}
		// check if articleId exists
		Article article = getArticleById(articleId);
		if (article == null) {
			throw new ResourceNotFoundException(stringArticleId + articleId);
		}
		// check if author of update request is the article author
		if (!updateArticle.getAuthorId().equals(article.getAuthorId())) {
			throw new AccessDeniedException(stringAuthorId + updateArticle.getAuthorId() + " is not the author of this article");
		}

		// if any of title, content, category not null -> update
		if (updateArticle.getTitle() != null) {
			article.setTitle(updateArticle.getTitle());
		}
		if (updateArticle.getContent() != null) {
			article.setContent(updateArticle.getContent());
		}
		if (updateArticle.getCategories() != null) {
			// check if category names in update request exist in db-categories collection
			List<String> updateCategoryNames = categoryFilter(updateArticle.getCategories());
			if(!isCategoryListExist(updateCategoryNames)) {
				throw new ResourceNotFoundException("categories-" + updateCategoryNames);
			}
			// find categories from db that matched categories in update request
			List<Category> categoryList = categoryRepository.findByCategoryNameIn(updateCategoryNames);
			article.setCategories(categoryList);
		}
		articleRepository.save(article);
	}

	// Only article author can delete the article
	@Override
	public void deleteArticle(String articleId) {
		// check if articleId exists
		Article article = articleRepository.findByArticleId(new ObjectId(articleId));
		if (article == null) {
			throw new ResourceNotFoundException(stringArticleId + articleId);
		}
		// delete article
		articleRepository.deleteById(new ObjectId(articleId));
		// and delete all comments of this article
		commentRepository.deleteAllByArticleId(articleId);
	}

	@Override
	public Comment createComment(String articleId, Comment comment) {
		// check if articleId exists or not
		Article article = articleRepository.findByArticleId(new ObjectId(articleId));
		if (article == null) {
			throw new ResourceNotFoundException(stringArticleId + articleId);
		}
		// check if userId or content null or not
		if (comment.getUserId() == null || comment.getContent() == null) {
			throw new BadRequestException("userId/content can not null");
		}
		// check userId in db
		User persistUser = userRepository.findUserById(comment.getUserId());
		if (persistUser == null) {
			throw new ResourceNotFoundException("userId-" + comment.getUserId());
		}


		// comment currently has content and userId
		// set userName, articleId, commentedAt
		comment.setArticleId(articleId);
		comment.setUserName(persistUser.getName());
		LocalDateTime commentedAt = LocalDateTime.now();
		comment.setCommentedAt(commentedAt);

		Comment savedComment = commentRepository.save(comment);

		// add top 10 latest comments to article
		// new comment will insert in 0 index of list comment
		if (article.getComments() == null) {
			article.setComments(Collections.singletonList(savedComment));
		} else if (article.getComments().toArray().length < 10) {
			article.getComments().add(0, savedComment);
		} else {
			// remove the oldest comment and insert new one to index 0
			article.getComments().remove(9);
			article.getComments().add(0, savedComment);
		}
		articleRepository.save(article);
		return savedComment;
	}

	@Override
	public void updateComment(String articleId, String commentId, String userUpdateId, String status) {
		// find article that matched articleId
		Article article = getArticleById(articleId);
		if (article == null) {
			throw new ResourceNotFoundException(stringArticleId + articleId);
		}
		Comment comment = getCommentById(commentId);
		if (comment == null) {
			throw new ResourceNotFoundException("commentId-" + commentId);
		}
		if (!article.getAuthorId().equals(userUpdateId)) {
			throw new AccessDeniedException("userUpdateId-" + userUpdateId + " is not author of this article");
		}
		if (status == null) {
			throw new BadRequestException("status can not null");
		} else {
			comment.setStatus(status.toLowerCase());
			if (comment.getArticleId().equals(articleId)) {
				// update status of comment in article collection
				articleRepository.updateCommentStatusByCommentId(articleId, commentId, status.toLowerCase());
			}
			commentRepository.save(comment);
		}

	}
	// Only author of article can delete comment
	@Override
	public void deleteComment(String articleId, String commentId) {
		Article article = getArticleById(articleId);
		if (article == null) {
			throw new ResourceNotFoundException(stringArticleId + articleId);
		}
		Comment comment = getCommentById(commentId);
		if (comment == null) {
			throw new ResourceNotFoundException("commentId-" + commentId);
		}
		if (comment.getArticleId().equals(articleId)) {
			articleRepository.deleteCommentByCommentId(articleId, commentId);
		}
		commentRepository.deleteById(new ObjectId(commentId));
	}


	public Article getArticleById(String articleId) {
		return articleRepository.findByArticleId(new ObjectId(articleId));
	}

	public Comment getCommentById(String commentId) {
		return commentRepository.findByCommentId(new ObjectId(commentId));
	}

	public boolean isArticleTitleAlreadyExisted(String title) {
		Optional<Article> optionalArticle = articleRepository.findArticleByTitle(title);
		return optionalArticle.isPresent();
	}

	public boolean isCategoryListExist(List<String> categories) {
		List<String> categoryList = articleRepository.getListCategoryName();
		for (String categoryName : categories) {
			if (!categoryList.contains(categoryName)) {
				return false;
			}
		}
		return true;
	}

	public List<String> categoryFilter(List<Category> categories) {
		// To lower category name and
		// remove duplicate category name
		return categories.stream()
				.map( category -> category.getCategoryName().toLowerCase())
				.distinct()
				.collect(Collectors.toList());
	}
}
