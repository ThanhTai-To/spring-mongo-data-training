package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Comment;
import com.pycogroup.superblog.model.User;
import com.pycogroup.superblog.repository.ArticleRepository;
import com.pycogroup.superblog.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class ArticleServiceImp implements ArticleService {
	@Autowired
	private ArticleRepository articleRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	MongoOperations mongoOperations;

	@Override
	public List<Article> getAllArticles() {
		return articleRepository.findAll();
	}

	@Override
	public Article createArticle(Article article) {
		// Check if author email in db
		User currentUser =  userRepository.findByEmail(article.getAuthorEmail());
		if (currentUser == null) {
			throwExceptions(HttpStatus.NOT_FOUND, article.getAuthorEmail() + " not found");
		}
		// Check if article title already existed
		Optional<Article> existedArticleTitle = articleRepository.findArticleByTitle(article.getTitle());
		if (existedArticleTitle.isPresent()) {
			throwExceptions(HttpStatus.CONFLICT, "title " + article.getTitle() + " already existed");
		}

		// create articleId index
		String uniqueId = UUID.randomUUID().toString();
		// Check if uniqueID already existed
		Article existedArticleId = articleRepository.findArticleByArticleId(uniqueId);
		if (existedArticleId != null) {
			throwExceptions(HttpStatus.CONFLICT, "articleId " + uniqueId + " already existed");
		}
		article.setArticleId(uniqueId);
		return articleRepository.save(article);
	}

	@Override
	public String createComment(String articleId, Comment comment) {
		// check userEmail and userName null or not
		if (comment.getUserEmail() == null || comment.getUserName() == null || comment.getContent() == null) {
			throwExceptions(HttpStatus.BAD_REQUEST, "userEmail/userName/content can not null");
		}
		// check userEmail in db
		User persistentUser = userRepository.findByEmail(comment.getUserEmail());
		if (persistentUser == null) {
			throwExceptions(HttpStatus.NOT_FOUND, "userEmail " + comment.getUserEmail() + " does not found");
		}
		// check userName matched
		if (!persistentUser.getName().equals(comment.getUserName())) {
			throwExceptions(HttpStatus.CONFLICT, "userName " + comment.getUserEmail() + " does not " +
					"\nhave the name " + comment.getUserName());
		}

		// find article by articleId
		Article article = mongoOperations.findOne(new Query(Criteria.where("articleId").is(articleId)), Article.class);
		if (article == null) {
			throwExceptions(HttpStatus.NOT_FOUND, articleId + " does not found");
		}
		// set commentId for comment
		String commentId = UUID.randomUUID().toString();
		comment.setCommentId(commentId);
		// add comment to articles
		if (article.getComments() == null) {
			article.setComments(Arrays.asList(comment));
		} else {
			article.getComments().add(comment);
		}
		mongoOperations.save(article);
		return comment.getCommentId();
	}

	// TODO: update category, OR delete old and add new
	@Override
	public void updateArticle(String articleId, Article article) {
		// Check if article in db
		if (isArticleExist(articleId)) {
			// Find article that matched articleId
			Query query = new Query();
			query.addCriteria(Criteria.where("articleId").is(articleId));
			query.fields().include("articleId");
			// Update the fields that are not null
			Update update = new Update();
			if (article.getTitle() != null) {
				update.set("title", article.getTitle());
			}
			if (article.getCategories() != null) {
				update.set("categories", article.getCategories());
			}
			if (article.getContent() != null) {
				update.set("content", article.getContent());
			}
			mongoOperations.updateFirst(query, update, Article.class);
		}
	}

	@Override
	public void updateComment(String articleId, String commentId, String updateCommentAuthorEmail, Comment updateComment) {
		if (isCommentExist(articleId, commentId)) {
			if (updateCommentAuthorEmail == null) {
				throwExceptions(HttpStatus.BAD_REQUEST, "authorEmail can not null");
			}
			// Only article author can update status, otherwise throw exception
			Article article = articleRepository.findArticleByArticleId(articleId);
			if (!updateCommentAuthorEmail.equals(article.getAuthorEmail())) {
				throwExceptions(HttpStatus.FORBIDDEN, updateCommentAuthorEmail + " does not allow to update");
			}

			// update status field if the request is not null
			Query query = new Query();
			Update update = new Update();
			if (updateComment.getStatus() != null) {
				query.addCriteria(Criteria.where("articleId").is(articleId));
				query.addCriteria(Criteria.where("comments.commentId").is(commentId));
				update.set("comments.$.status", updateComment.getStatus());
			}
			mongoOperations.findAndModify(query, update, Article.class);
		}
	}

	@Override
	public void deleteArticle(String articleId) {
		Article article = articleRepository.findArticleByArticleId(articleId);
		if (article == null) {
			throwExceptions(HttpStatus.NOT_FOUND, articleId + " does not found");
		}
		articleRepository.deleteByArticleId(articleId);
	}

	private void throwExceptions(HttpStatus status, String message) {
		throw new ResponseStatusException(
				status, message
		);
	}

	// true = exist, otherwise it will throw exception
	private boolean isArticleExist(String articleId) {
		Article existedArticle = articleRepository.findArticleByArticleId(articleId);
		if (existedArticle == null) {
			throwExceptions(HttpStatus.NOT_FOUND, "articleId" + articleId + " does not found");
		}
		return true;
	}

	private boolean isCommentExist(String articleId, String commentId) {
		if (isArticleExist(articleId)) {
			Optional<Article> article = articleRepository.findByArticleIdAndCommentsCommentId(articleId, commentId);
			if(!article.isPresent()) {
				throwExceptions(HttpStatus.NOT_FOUND, "commentId "+ commentId + " does not found");
			}
		}
		return true;
	}
}
