package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.User;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ArticleServiceImplTest {
    private static final int INIT_ARTICLE_NUMBER = 2;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ArticleServiceImpl articleServiceImpl;

    @Before
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
        List<Article> articleList = articleServiceImpl.getAllArticles();
        Assert.assertEquals(INIT_ARTICLE_NUMBER, articleList.toArray().length);
    }

}
