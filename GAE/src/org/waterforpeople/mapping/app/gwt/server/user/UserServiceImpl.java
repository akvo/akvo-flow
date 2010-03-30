package org.waterforpeople.mapping.app.gwt.server.user;

import java.util.ArrayList;
import java.util.List;

import org.waterforpeople.mapping.app.gwt.client.user.UserConfigDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;

import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserConfig;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Retrieves users and their personalization info from the data store
 * 
 * @author Christopher Fagiani
 * 
 */
public class UserServiceImpl extends RemoteServiceServlet implements
		UserService {

	private static final long serialVersionUID = 5371988266704758230L;
	private UserDao userDao;

	public UserServiceImpl() {
		userDao = new UserDao();
	}

	/**
	 * lists all users in the system.
	 */
	@Override
	public UserDto[] listUser() {
		List<User> users = userDao.list();
		UserDto[] result = null;
		if (users != null) {
			result = new UserDto[users.size()];
			for (int i = 0; i < users.size(); i++) {
				UserDto dto = new UserDto();
				dto.setUserName(users.get(i).getUserName());
				dto.setEmailAddress(users.get(i).getEmailAddress());
				result[i] = dto;
			}
		}
		return result;
	}

	/**
	 * returns the UserConfigDto for the currently logged in user. If the
	 * current user is not found in the database, a User record will be
	 * autocreated. If no config info is present (or if this is a new user),
	 * this method returns null.
	 */
	@Override
	public UserDto getCurrentUserConfig() {
		com.google.appengine.api.users.UserService userService = UserServiceFactory
				.getUserService();
		com.google.appengine.api.users.User currentUser = userService
				.getCurrentUser();

		UserDto userDto = new UserDto();
		if (currentUser != null) {
			User u = userDao.findUserByEmail(currentUser.getEmail());
			if (u == null) {
				User newUser = new User();
				newUser.setEmailAddress(currentUser.getEmail());
				newUser.setUserName(currentUser.getNickname());
				userDto.setEmailAddress(currentUser.getEmail());
				userDto.setUserName(currentUser.getNickname());
				userDao.save(newUser);
			} else {
				List<UserConfigDto> configList = new ArrayList<UserConfigDto>();
				if (u.getConfig() != null) {
					for (UserConfig c : u.getConfig()) {
						UserConfigDto confDto = new UserConfigDto();
						confDto.setGroup(c.getGroup());
						confDto.setName(c.getName());
						confDto.setValue(c.getValue());
						configList.add(confDto);
					}
					userDto.setConfig(configList);
					userDto.setUserName(u.getUserName());
					userDto.setEmailAddress(u.getEmailAddress());
				}
			}
		}
		return userDto;
	}

	@Override
	public void saveUser(UserDto user) {
		User existingUser = userDao.findUserByEmail(user.getEmailAddress());
		User newUser = new User();
		if (existingUser != null) {
			newUser = existingUser;
		}
		if (user.getEmailAddress() != null) {
			newUser.setEmailAddress(user.getEmailAddress());
		}
		if (user.getUserName() != null) {
			newUser.setUserName(user.getUserName());
		}
		if (user.getConfig() != null) {
			List<UserConfig> confList = new ArrayList<UserConfig>();
			for (UserConfigDto confDto : user.getConfig()) {
				UserConfig config = new UserConfig();
				config.setGroup(confDto.getGroup());
				config.setName(confDto.getName());
				config.setValue(confDto.getValue());
				confList.add(config);
			}
			newUser.setConfig(confList);
		}	
			userDao.save(newUser);	
	}

}
