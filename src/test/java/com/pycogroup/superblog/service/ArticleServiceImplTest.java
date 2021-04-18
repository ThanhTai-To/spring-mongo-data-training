package com.pycogroup.superblog.service;

import com.pycogroup.superblog.exception.AccessDeniedException;
import com.pycogroup.superblog.exception.AlreadyExistedException;
import com.pycogroup.superblog.exception.BadRequestException;
import com.pycogroup.superblog.exception.ResourceNotFoundException;
import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Category;
import com.pycogroup.superblog.model.Comment;
import com.pycogroup.superblog.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;

//@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ArticleServiceImplTest {
    private static final int INIT_ARTICLE_NUMBER = 2;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ArticleServiceImpl articleServiceImpl;

    @Rule
    public final ExpectedException exception = ExpectedException.none();

//    @Before
    public void init() {
        mongoTemplate.remove(Article.class).all();
        mongoTemplate.remove(User.class).all();
        User author = mongoTemplate.save(User.builder()
                .name(RandomStringUtils.random(40))
                .email("fake"+RandomStringUtils.randomAlphabetic(5)+ "@.local")
                .build());
        for (int i = 0; i < INIT_ARTICLE_NUMBER; i++) {
            Article article = Article.builder()
                    .content(RandomStringUtils.random(40))
                    .title(RandomStringUtils.random(500))
                    .authorId(author.getId().toString())
                    .authorName(author.getName())
                    .build();
            mongoTemplate.save(article);
        }
    }

    @Test
    public void testGetAllArticles() {
        init();
        List<Article> articleList = articleServiceImpl.getAllArticles();
        Assertions.assertEquals(INIT_ARTICLE_NUMBER, articleList.toArray().length);
    }

    @Test
    public void testCreateArticleWithAuthorNull() {
        mongoTemplate.remove(Article.class);
        Article article = Article.builder()
                .content("content 1")
                .authorId("07b9e2d7f55842e9de45ce31")
                .build();
        try {
            articleServiceImpl.createArticle(article);
        } catch(ResourceNotFoundException ex) {}
    }

    @Test
    public void testCreateArticleWithCategoriesNotFound() {
        mongoTemplate.remove(Article.class);
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Category category = Category.builder()
                .categoryName(RandomStringUtils.random(30))
                .build();
        Article article = Article.builder()
                .content("content 1")
                .title(RandomStringUtils.random(30))
                .authorId(user.getId().toString())
                .categories(Collections.singletonList(category))
                .build();
        try {
            articleServiceImpl.createArticle(article);
        } catch(ResourceNotFoundException ex) {}

    }

    @Test
    public void testCreateArticleWithTitleAlreadyExisted() {
        mongoTemplate.remove(Article.class);
        Category category = Category.builder()
                .categoryName("lorem")
                .build();
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = mongoTemplate.save(Article.builder()
                .content("content 1")
                .title("title 1")
                .categories(Collections.singletonList(category))
                .authorId(user.getId().toString())
                .build());
        try {
            articleServiceImpl.createArticle(article);
        } catch(AlreadyExistedException ex) {}
    }


    @Test
    public void testCreateArticleWithCategoryNull() {
        mongoTemplate.remove(Article.class);
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = Article.builder()
                .content("content 1")
                .title("title 10")
                .authorId(user.getId().toString())
                .categories(null)
                .build();
        try {
            articleServiceImpl.createArticle(article);
        } catch(BadRequestException ex) {}
    }

    @Test
    public void testUpdateArticle() {
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());

        Article article = mongoTemplate.save(Article.builder()
                .content(RandomStringUtils.random(20))
                .authorId(user.getId().toString())
                .categories(null)
                .build());
        Category category = Category.builder()
                .categoryName(RandomStringUtils.random(20))
                .build();
        // Update Article without contain authorId
        Article updateArticleNoAuthor = Article.builder()
                .title(RandomStringUtils.random(30))
                .content("lorem")
                .categories(Collections.singletonList(category))
                .build();
        try {
            articleServiceImpl.updateArticle(article.getArticleId().toString(), updateArticleNoAuthor);
        } catch(BadRequestException e){}

        // Update Article with authorId not found
        Article updateArticleAuthorIdNotFound = Article.builder()
                .title(RandomStringUtils.random(30))
                .content("lorem")
                .authorId("07b9e2d7f55842e9de45ce31")
                .categories(Collections.singletonList(category))
                .build();
        try {
            articleServiceImpl.updateArticle(article.getArticleId().toString(), updateArticleAuthorIdNotFound);
        } catch(ResourceNotFoundException e){}

        // Update Article with articleId not found
        Article updateArticle = Article.builder()
                .title(RandomStringUtils.random(30))
                .content("lorem")
                .authorId(user.getId().toString())
                .categories(Collections.singletonList(category))
                .build();
        try {
            articleServiceImpl.updateArticle("07b9e2d7f55842e9de45ce31", updateArticle);
        } catch(ResourceNotFoundException e){}

        // Update Article with userId is not author of article
        User user2 = mongoTemplate.save(User.builder().name("thanhtai1789").build());
        Article updateArticleWrongAuthor = Article.builder()
                .title(RandomStringUtils.random(30))
                .content("lorem")
                .authorId(user2.getId().toString())
                .categories(Collections.singletonList(category))
                .build();
        try {
            articleServiceImpl.updateArticle(article.getArticleId().toString(), updateArticleWrongAuthor);
        } catch(AccessDeniedException e){}

        // Update Article with categories input not found
        Article updateArticleCategoriesNotFound = Article.builder()
                .title(RandomStringUtils.random(30))
                .content("lorem")
                .authorId(user.getId().toString())
                .categories(Collections.singletonList(category))
                .build();
        try {
            articleServiceImpl.updateArticle(article.getArticleId().toString(), updateArticleCategoriesNotFound);
        } catch(ResourceNotFoundException e){}

        // Update success
        Category savedCategory = mongoTemplate.save(category);
        Article updateArticleSucess = Article.builder()
                .title(RandomStringUtils.random(30))
                .content("lorem")
                .authorId(user.getId().toString())
                .categories(Collections.singletonList(savedCategory))
                .build();
        articleServiceImpl.updateArticle(article.getArticleId().toString(), updateArticleSucess);
        Query query = new Query();
        query.addCriteria(Criteria.where("articleId").is(article.getArticleId()));
        Article updatedArticle = mongoTemplate.findOne(query, Article.class);
        Assertions.assertEquals(updateArticleSucess.getTitle(), updatedArticle.getTitle());
        Assertions.assertEquals(updateArticleSucess.getContent(), updatedArticle.getContent());
        Assertions.assertEquals(updateArticleSucess.getCategories().size(), updatedArticle.getCategories().size());



    }

    @Test
    public void testGetArticleById() {
        mongoTemplate.remove(Article.class);
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = mongoTemplate.save(Article.builder()
                .content("content 1")
                .authorId(user.getId().toString())
                .categories(null)
                .build());
        Article actual = articleServiceImpl.getArticleById(article.getArticleId().toString());
        Assertions.assertEquals(article.getArticleId().toString(), actual.getArticleId().toString());
    }

    @Test
    public void testDeleteArticle() {
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = mongoTemplate.save(Article.builder()
                .content(RandomStringUtils.random(20))
                .authorId(user.getId().toString())
                .categories(null)
                .build());
        articleServiceImpl.deleteArticle(article.getArticleId().toString());
        Assertions.assertNull(mongoTemplate.findById(article.getArticleId(), Article.class));
        Query query = new Query();
        query.addCriteria(Criteria.where("articleId").is(article.getArticleId()));
        List<Comment> commentsContainOfDeletedArticle = mongoTemplate.find(query, Comment.class);
        Assertions.assertTrue(commentsContainOfDeletedArticle.isEmpty());
        // Delete with articleId not found
        try {
            articleServiceImpl.deleteArticle("07b9e2d7f55842e9de45ce31");
        } catch(ResourceNotFoundException e){}

    }


    @Test
    public void testIsCategoryListExist() {
        List<String> categories = Collections.singletonList("weather");
        Assertions.assertFalse(articleServiceImpl.isCategoryListExist(categories));
    }

    @Test
    public void testGetCommentById() {
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = mongoTemplate.save(Article.builder()
                .content("content 1")
                .authorId(user.getId().toString())
                .categories(null)
                .build());
        Comment comment = mongoTemplate.save(Comment.builder()
                .articleId(article.getArticleId().toString())
                .content("comment 1")
                .userId(user.getId().toString())
                .userName(user.getName())
                .build());
        Comment actual = articleServiceImpl.getCommentById(comment.getCommentId());
        Assertions.assertNotNull(actual);
    }

    @Test
    public void testDeleteComment() {
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = mongoTemplate.save(Article.builder()
                .content("content 1")
                .authorId(user.getId().toString())
                .categories(null)
                .build());
        Comment comment = mongoTemplate.save(Comment.builder()
                .articleId(article.getArticleId().toString())
                .content("comment 1")
                .userId(user.getId().toString())
                .userName(user.getName())
                .build());
        articleServiceImpl.deleteComment(article.getArticleId().toString(), comment.getCommentId());
        Assertions.assertNull(mongoTemplate.findById(comment.getCommentId(), Comment.class));
        Query query = new Query();
        query.addCriteria(Criteria.where("comments.$.commentId").is(comment.getCommentId()));
        List<Article> articleDeletedComment = mongoTemplate.find(query, Article.class);
        Assertions.assertTrue(articleDeletedComment.isEmpty());
    }

    @Test
    public void testDeleteCommentWithArticleIdNotFound() {
        User user = mongoTemplate.save(User.builder().name("thanhtai").build());
        Article article = mongoTemplate.save(Article.builder()
                .content("content 1")
                .authorId(user.getId().toString())
                .categories(null)
                .build());
        Comment comment = mongoTemplate.save(Comment.builder()
                .articleId(article.getArticleId().toString())
                .content("comment 1")
                .userId(user.getId().toString())
                .userName(user.getName())
                .build());
        try {
            articleServiceImpl.deleteComment("07b9e2d7f55842e9de45ce31", "07b9e2d7f55842e9de45ce31");
        } catch(ResourceNotFoundException e) {}
        try {
            articleServiceImpl.deleteComment(article.getArticleId().toString(), "07b9e2d7f55842e9de45ce31");
        } catch(ResourceNotFoundException e) {}
    }

}
