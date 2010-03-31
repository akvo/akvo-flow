package com.gallatinsystems.user.dao;

import com.gallatinsystems.framework.dao.BaseDAO;
import com.gallatinsystems.user.domain.User;

/**
 * Dao for User objects
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserDao extends BaseDAO<User> {

	public UserDao() {
		super(User.class);
	}

	/**
	 * finds a single user by email address.
	 * 
	 * @param email
	 * @return
	 */
	public User findUserByEmail(String email) {
		return findByProperty("emailAddress", email, STRING_TYPE);
	}
}
