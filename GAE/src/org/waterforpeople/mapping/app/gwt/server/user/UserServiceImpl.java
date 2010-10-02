package org.waterforpeople.mapping.app.gwt.server.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.waterforpeople.mapping.app.gwt.client.user.UserConfigDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserDto;
import org.waterforpeople.mapping.app.gwt.client.user.UserService;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
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
	 * searches for users from the datastore. The users returned contain just
	 * the core user info (not their config objects).
	 */
	public ResponseDto<ArrayList<UserDto>> listUsers(String userName,
			String emailAddress, String sortBy, String sortDir, String cursor) {
		List<User> users = userDao.searchUser(userName, emailAddress, sortBy,
				sortDir, cursor);
		ArrayList<UserDto> dtoList = new ArrayList<UserDto>();
		ResponseDto<ArrayList<UserDto>> response = new ResponseDto<ArrayList<UserDto>>();
		if (users != null) {
			for (User u : users) {
				UserDto dto = new UserDto();
				dto.setKeyId(u.getKey().getId());
				dto.setUserName(u.getUserName());
				dto.setEmailAddress(u.getEmailAddress());
				dtoList.add(dto);
			}
			response.setCursorString(UserDao.getCursor(users));
		}
		response.setPayload(dtoList);
		return response;

	}

	/**
	 * lists all users in the system.
	 */
	@Override
	public UserDto[] listUser() {
		List<User> users = userDao.list(Constants.ALL_RESULTS);
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
	 * auto-created. If no config info is present (or if this is a new user),
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
				Map<String, Set<UserConfigDto>> configMap = new HashMap<String, Set<UserConfigDto>>();

				if (u.getConfig() != null) {
					for (UserConfig c : u.getConfig()) {
						UserConfigDto confDto = new UserConfigDto();
						confDto.setGroup(c.getGroup());
						confDto.setName(c.getName());
						confDto.setValue(c.getValue());
						Set<UserConfigDto> dtoList = configMap
								.get(c.getGroup());
						if (dtoList == null) {
							dtoList = new HashSet<UserConfigDto>();
							configMap.put(c.getGroup(), dtoList);
						}
						dtoList.add(confDto);
					}
					userDto.setConfig(configMap);
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
			for (String key : user.getConfig().keySet()) {
				// flush old config values for the group
				if (existingUser != null && existingUser.getConfig() != null) {
					for (UserConfig oldConf : existingUser.getConfig()) {
						userDao.delete(oldConf);
					}
				}
				for (UserConfigDto confDto : user.getConfig().get(key)) {
					UserConfig config = new UserConfig();
					config.setGroup(confDto.getGroup());
					config.setName(confDto.getName());
					config.setValue(confDto.getValue());
					confList.add(config);
				}
			}

			newUser.setConfig(confList);
		}
		userDao.save(newUser);
	}

	/**
	 * deletes a config from the datastore
	 * 
	 * @param dto
	 * @param emailAddress
	 */
	public void deletePortletConfig(UserConfigDto dto, String emailAddress) {
		UserConfig c = findUserConfig(emailAddress, dto.getGroup(), dto
				.getName(), false);
		if (c != null) {
			userDao.delete(c);
		}
	}

	/**
	 * updates a single userConfig object to an existing user. both the user and
	 * the config object must exist to use this method
	 * 
	 * @param emailAddress
	 * @param configGroup
	 * @param confDto
	 */
	public void updateUserConfigItem(String emailAddress, String configGroup,
			UserConfigDto confDto) {
		UserConfig conf = findUserConfig(emailAddress, configGroup, confDto
				.getName(), true);
		if (conf != null) {
			// update the value and persist
			conf.setValue(confDto.getValue());
			userDao.save(conf);
		}
	}

	/**
	 * finds a single userConfig item by name
	 * 
	 * @param emailAddress
	 * @param configGroup
	 * @param configName
	 * @return
	 */
	public UserConfigDto findUserConfigItem(String emailAddress,
			String configGroup, String configName) {
		UserConfig c = findUserConfig(emailAddress, configGroup, configName,
				false);
		UserConfigDto dto = null;
		if (c != null) {
			dto = new UserConfigDto();
			dto.setGroup(c.getGroup());
			dto.setName(c.getName());
			dto.setValue(c.getValue());
		}
		return dto;
	}

	/**
	 * finds a userConfig object by its group and name
	 * 
	 * @param emailAddress
	 * @param configGroup
	 * @param configName
	 * @return
	 */
	private UserConfig findUserConfig(String emailAddress, String configGroup,
			String configName, boolean createIfMissing) {
		User user = userDao.findUserByEmail(emailAddress);
		if (user != null && user.getConfig() != null) {
			for (UserConfig confItem : user.getConfig()) {
				if (configGroup != null
						&& configGroup.equals(confItem.getGroup())) {
					if (confItem.getName() != null
							&& confItem.getName().equals(configName)) {
						return confItem;
					}
				}
			}
		}
		// if we got here, then it's missing
		if (createIfMissing) {
			UserConfig confItem = new UserConfig();
			confItem.setCreatedDateTime(new Date());
			confItem.setGroup(configGroup);
			confItem.setName(configName);
			if (user.getConfig() == null) {
				user.setConfig(new ArrayList<UserConfig>());
			}
			user.getConfig().add(confItem);
			userDao.save(user);
			return confItem;
		} else {
			return null;
		}
	}

	/**
	 * deletes a user identified by the id passed in
	 * 
	 * @param userId
	 */
	public void deleteUser(Long userId) {
		User u = userDao.getByKey(userId);
		if (u != null) {
			userDao.delete(u);
		}
	}

}