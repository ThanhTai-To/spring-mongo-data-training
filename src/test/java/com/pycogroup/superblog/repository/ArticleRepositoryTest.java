package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.Address;
import com.pycogroup.superblog.model.Article;
import com.pycogroup.superblog.model.Category;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ArticleRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private ArticleRepository articleRepository;

    @Before
    public void init() {
        mongoTemplate.remove(User.class).all();
        User author = mongoTemplate.save(User.builder()
                .name(RandomStringUtils.random(40))
                .email("fake"+RandomStringUtils.randomAlphabetic(5)+ "@.local")
                .build());

        mongoTemplate.remove(Category.class).all();
        Category category = mongoTemplate.save(Category.builder()
                .categoryName(RandomStringUtils.random(40))
                .build());

        mongoTemplate.remove(Article.class).all();
        mongoTemplate.save(Article.builder()
                    .title(RandomStringUtils.random(30))
                    .content(RandomStringUtils.random(40))
                    .authorId(author.getId().toString())
                    .authorName(author.getName())
                    .categories(Collections.singletonList(category))
                    .build());
    }

    @Test
    public void testFindAll() {
        List<Article> articleList = articleRepository.findAll();
        Assert.assertEquals(1, articleList.toArray().length);
    }

}
