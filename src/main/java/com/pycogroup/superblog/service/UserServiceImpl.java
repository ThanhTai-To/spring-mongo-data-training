package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.QUser;
import com.pycogroup.superblog.model.User;
import com.pycogroup.superblog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User createUser(User user) {
		return userRepository.save(user);
	}

	public User findUserByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Override
	public List<User> findUsersByPrefixName(String prefix) {
		QUser userQuery = QUser.user;
		return (List<User>) userRepository.findAll(userQuery.name.startsWith(prefix));

	}
}
