package com.pycogroup.superblog.service;

import com.pycogroup.superblog.model.User;

import java.util.List;

public interface UserService {
	List<User> getAllUsers();
	User createUser(User user);
}
