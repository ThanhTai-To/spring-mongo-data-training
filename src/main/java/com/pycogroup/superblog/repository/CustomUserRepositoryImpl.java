package com.pycogroup.superblog.repository;


import com.pycogroup.superblog.model.User;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Be noticed that the CustomUserRepositoryImpl = CustomUserRepository + Impl.
 * If you name it different, it doesn't work
 */
public class CustomUserRepositoryImpl implements  CustomUserRepository {


	private final MongoTemplate mongoTemplate;

	@Autowired
	public CustomUserRepositoryImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void banUserByEmail(String email) {
		Query query = new Query(Criteria.where("email").is(email));
		Update update = new Update();
		update.set("enabled", false);
		mongoTemplate.updateFirst(query, update, User.class);
	}

	@Override
	public User findUserById(String userId) {
		Query query = new Query(Criteria.where("_id").is(new ObjectId(userId)));
		return  mongoTemplate.findOne(query, User.class);
	}
}
