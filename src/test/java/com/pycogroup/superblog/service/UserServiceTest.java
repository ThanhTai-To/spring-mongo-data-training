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
public class UserServiceTest {
	private static final String TEST_USER_EMAIL = "test@local";

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private UserService userService;

	@Before
	public void init() {
		mongoTemplate.remove(User.class).all();
		mongoTemplate.save(User.builder()
			.name("A" + RandomStringUtils.random(40))
			.email(TEST_USER_EMAIL)
			.build());
		mongoTemplate.save(User.builder()
			.name("A" + RandomStringUtils.random(40))
			.email("A" + RandomStringUtils.random(40) + "@local.email")
			.build());
		mongoTemplate.save(User.builder()
			.name("B" + RandomStringUtils.random(40))
			.email("B" + RandomStringUtils.random(40) + "@local.email")
			.build());

	}

	@Test
	public void testFindAllMustReturnEnoughQuantity() {
		List<User> userList = userService.getAllUsers();
		Assert.assertEquals(3, userList.size());
	}

	@Test
	public void testFindByExistEmailMustReturnResult() {
		User user = userService.findUserByEmail(TEST_USER_EMAIL);
		Assert.assertNotNull(user);
		Assert.assertEquals(TEST_USER_EMAIL, user.getEmail());
	}

	@Test
	public void testFindUsersByPrefixNameMustReturnCorrectResult() {
		List<User> userList = userService.findUsersByPrefixName("A");
		Assert.assertEquals(2, userList.size());
		for (User user: userList) {
			Assert.assertTrue(user.getName().startsWith("A"));
		}
	}
}
