package com.gallatinsystems.user.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.user.domain.User;

public class UserDao extends BaseDAO<User> {

	public UserDao() {
		super(User.class);
	}

	public User findUserByEmail(String email) {
		return findByProperty("emailAddress", email, STRING_TYPE);
	}

}
