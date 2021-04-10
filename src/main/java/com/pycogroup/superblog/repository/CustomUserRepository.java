package com.pycogroup.superblog.repository;

import com.pycogroup.superblog.model.User;

public interface CustomUserRepository {
	void banUserByEmail(String email);
	User findUserById(String userId);
}
