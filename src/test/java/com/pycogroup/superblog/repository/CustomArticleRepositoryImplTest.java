package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Category;
import com.pycogroup.superblog.model.Comment;
import com.pycogroup.superblog.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class CustomArticleRepositoryImplTest {
    @Autowired
    private MongoOperations mongoOperations;

    @Autowired
    private ArticleRepository articleRepository;

    @Before
    public void init() {

        User author = initUser();
        List<Category> categories = initCategoryList();

        mongoOperations.remove(Article.class);
        Article article1 = mongoOperations.save(Article.builder()
                .title("Article 1")
                .content("Lorem ..")
                .authorId(author.getId().toString())
                .authorName(author.getName())
                .createdAt(LocalDateTime.now())
                .categories(categories)
                .build());
        Article article2 = mongoOperations.save(Article.builder()
                .title("Article 2")
                .content("Lorem ..")
                .authorId(author.getId().toString())
                .authorName(author.getName())
                .createdAt(LocalDateTime.now())
                .categories(categories)
                .build());

        mongoOperations.remove(Comment.class);
        Comment.builder()
                .content("Comment 1")
                .userId(author.getId().toString())
                .userName(author.getName())
                .articleId(article1.getArticleId().toString())
                .commentedAt(LocalDateTime.now())
                .build();



    }

    private User initUser() {
        mongoOperations.remove(User.class);
        return mongoOperations.save(User.builder()
                .name(RandomStringUtils.random(20))
                .email("fake"+RandomStringUtils.randomAlphabetic(5)+ "@.local")
                .build());
    }

    private Category initACategory() {
        return mongoOperations.save(Category.builder()
                .categoryName(RandomStringUtils.random(20))
                .build());
    }

    private List<Category> initCategoryList() {
        mongoOperations.remove(Category.class);
        List<Category> categories = new ArrayList<>();
        Category category1 = initACategory();
        Category category2 = initACategory();
        categories.add(category1);
        categories.add(category2);
        return categories;
    }

    private Article initArticle(User author) {
        List<Category> categories = initCategoryList();

        mongoOperations.remove(Article.class);
        return mongoOperations.save(Article.builder()
                .title(RandomStringUtils.random(30))
                .content(RandomStringUtils.random(40))
                .authorId(author.getId().toString())
                .authorName(author.getName())
                .createdAt(LocalDateTime.now())
                .categories(categories)
                .build());
    }

    @Test
    public void testUpdateArticlesCategoryName() {
        Category category = initACategory();
        articleRepository.updateArticlesCategoryName(category.getCategoryId().toString(), "education");
        Query query = new Query(Criteria.where("categories.categoryName").is(category.getCategoryName()));
        List<Article> articleList = mongoOperations.find(query, Article.class);
        Assert.assertNotNull(articleList);
        articleList.forEach(article -> {
           article.getCategories().forEach(articleCategory ->
                   Assert.assertEquals("education",articleCategory.getCategoryName()));
        });
    }

    @Test
    public void testGetListCategoryName() {
        List<String> actualCategoryNames = articleRepository.getListCategoryName();
        Query query = new Query();
        query.fields().include("categoryName").exclude("categoryId");
        List<String> expectedCategoryNames = mongoOperations.find(query, Category.class)
                .stream()
                .map(Category::getCategoryName)
                .collect(Collectors.toList());
        Assert.assertNotNull(actualCategoryNames);
        Assert.assertEquals(expectedCategoryNames.size(),actualCategoryNames.size());
        Assert.assertEquals(expectedCategoryNames, actualCategoryNames);
    }

    @Test
    public void testDeleteArticleCategoryByCategoryId() {
        List<Article> articles = mongoOperations.findAll(Article.class);
        Query query = new Query();
        List<ObjectId> categoryIdList = mongoOperations.find(query, Category.class)
                .stream()
                .map(Category::getCategoryId)
                .collect(Collectors.toList());
        String categoryId = categoryIdList.get(0).toString();

        articleRepository.deleteArticleCategoryByCategoryId(categoryId);

        Query query2 = new Query(Criteria.where("categories.categoryId").is(categoryId));
        List<Article> actual = mongoOperations.find(query2, Article.class);
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void testUpdateCommentStatusByCommentId() {
        User author = initUser();
        Article article = initArticle(author);
        mongoOperations.remove(Comment.class);
        User userComment = initUser();
        Comment comment = mongoOperations.save(Comment.builder()
                .content("Comment 1")
                .userId(author.getId().toString())
                .userName(userComment.getName())
                .articleId(article.getArticleId().toString())
                .commentedAt(LocalDateTime.now())
                .build());
        // Save comment to Article collection
        article.setComments(Collections.singletonList(comment));
        mongoOperations.save(article);
        // Update comment status in Article
        articleRepository.updateCommentStatusByCommentId(article.getArticleId().toString(), comment.getCommentId(), "approved");
        // Find comment that updated
        Query query = new Query(Criteria.where("comments.commentId").is(comment.getCommentId()));
        Article articleUpdatedCommentStatus = mongoOperations.findOne(query, Article.class);
        assert articleUpdatedCommentStatus != null;
        // Find which comment in article is equal to commentId that updated
        articleUpdatedCommentStatus.getComments().forEach(commentInArticle -> {
            if (commentInArticle.getCommentId().equals(comment.getCommentId())){
                Assert.assertEquals("approved", commentInArticle.getStatus());
            }
        });
    }

    @Test
    public void testDeleteCommentByCommentId() {
        User author = initUser();
        Article article = initArticle(author);
        mongoOperations.remove(Comment.class);
        User userComment = initUser();
        Comment comment = mongoOperations.save(Comment.builder()
                .content("Comment 2")
                .userId(author.getId().toString())
                .userName(userComment.getName())
                .articleId(article.getArticleId().toString())
                .commentedAt(LocalDateTime.now())
                .build());
        // Save comment to Article collection
        article.setComments(Collections.singletonList(comment));
        mongoOperations.save(article);

        articleRepository.deleteCommentByCommentId(article.getArticleId().toString(), comment.getCommentId());
        Query query = new Query(Criteria.where("comments.commentId").is(comment.getCommentId()));
        Article articleDeletedComment = mongoOperations.findOne(query, Article.class);
        assert articleDeletedComment == null;
    }

}
