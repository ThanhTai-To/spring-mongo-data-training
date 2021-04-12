package com.pycogroup.superblog.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
public class ArticleTest {

    @Test
    public void testArticle() {
        Article article = new Article();
        article.setTitle("Article");
        article.setContent("Lorem");
        article.setAuthorId("123456");
        Assert.assertEquals("Article", article.getTitle());
        Assert.assertEquals("Lorem", article.getContent());
        Assert.assertEquals("123456", article.getAuthorId());

        List<Category> categories = new ArrayList<Category>();
        Category category1 = Category.builder().categoryName("food").build();
        Category category2 = Category.builder().categoryName("education").build();
        categories.add(category1);
        categories.add(category2);
        article.setCategories(categories);
        Assert.assertEquals(categories, article.getCategories());

        Comment comment = Comment.builder()
                .content("Comment 1")
                .userId("1")
                .userName("1")
                .articleId("1")
                .commentedAt(LocalDateTime.now())
                .build();
        article.setComments(Collections.singletonList(comment));
        Assert.assertEquals(Collections.singletonList(comment), article.getComments());
    }
}
