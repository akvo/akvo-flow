/*
 *  Copyright (C) 2010-2012 Stichting Akvo (Akvo Foundation)
 *
 *  This file is part of Akvo FLOW.
 *
 *  Akvo FLOW is free software: you can redistribute it and modify it under the terms of
 *  the GNU Affero General Public License (AGPL) as published by the Free Software Foundation,
 *  either version 3 of the License or any later version.
 *
 *  Akvo FLOW is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Affero General Public License included below for more details.
 *
 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package com.gallatinsystems.user.app.gwt.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.waterforpeople.mapping.app.util.DtoMarshaller;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.framework.gwt.dto.client.ResponseDto;
import com.gallatinsystems.user.app.gwt.client.PermissionDto;
import com.gallatinsystems.user.app.gwt.client.UserConfigDto;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.app.gwt.client.UserService;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.Permission;
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

	private static Logger log = Logger.getLogger("UserServiceImpl");
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
				dto.setPermissionList(u.getPermissionList());
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
				dto.setKeyId(users.get(i).getKey().getId());
				dto.setPermissionList(users.get(i).getPermissionList());
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
	public UserDto getCurrentUserConfig(boolean createIfNotFound) {
		com.google.appengine.api.users.UserService userService = UserServiceFactory
				.getUserService();
		com.google.appengine.api.users.User currentUser = userService
				.getCurrentUser();

		UserDto userDto = new UserDto();
		if (currentUser != null) {

			User u = null;
			if (currentUser.getEmail() != null
					&& currentUser.getEmail().trim().length() > 0) {
				u = userDao.findUserByEmail(currentUser.getEmail());
				if (u == null) {
					u = userDao.findUserByEmail(currentUser.getEmail()
							.toLowerCase());
				}
			}
			if (u == null && (createIfNotFound || userService.isUserAdmin())) {
				User newUser = new User();
				newUser.setEmailAddress(currentUser.getEmail());
				newUser.setUserName(currentUser.getNickname());
				userDto.setEmailAddress(currentUser.getEmail());
				userDto.setUserName(currentUser.getNickname());
				newUser = userDao.save(newUser);
				userDto.setKeyId(newUser.getKey().getId());
			} else if (u != null) {
				log.log(Level.SEVERE,
						"user not found in database using email: "
								+ currentUser.getEmail());
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
				}
				userDto.setUserName(u.getUserName());
				userDto.setEmailAddress(u.getEmailAddress());
				userDto.setPermissionList(u.getPermissionList());
				userDto.setAdmin(userService.isUserAdmin());
				if(u.isSuperAdmin()!=null && u.isSuperAdmin()){
					userDto.setSuperAdmin(true);
				}
				userDto.setKeyId(u.getKey().getId());
			} else {
				userDto.setHasAccess(false);
			}
			userDto.setLogoutUrl(userService.createLogoutURL("/logout.html"));
		}
		return userDto;
	}

	/**
	 * saves a user to the datastore. If a user already exists with the same
	 * email address, it will be updated to prevent duplicates. this method will
	 * also save any configuration and/or permissions passed in.
	 */
	@Override
	public void saveUser(UserDto user) {
		User existingUser = userDao.findUserByEmail(user.getEmailAddress());
		if (existingUser == null) {
			existingUser = userDao.findUserByEmail(user.getEmailAddress()
					.toLowerCase());
		}
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
		if (user.getPermissionList() != null) {
			newUser.setPermissionList(user.getPermissionList());
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
		UserConfig c = findUserConfig(emailAddress, dto.getGroup(),
				dto.getName(), false);
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
		UserConfig conf = findUserConfig(emailAddress, configGroup,
				confDto.getName(), true);
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
		if (user == null) {
			user = userDao.findUserByEmail(emailAddress.toLowerCase());
		}
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

	/**
	 * lists all permissions
	 * 
	 * @return
	 */
	public List<PermissionDto> listPermissions() {
		List<Permission> permissionList = userDao.listPermissions();
		List<PermissionDto> dtoList = new ArrayList<PermissionDto>();
		if (permissionList != null) {
			for (Permission p : permissionList) {
				PermissionDto pd = new PermissionDto();
				DtoMarshaller.copyToDto(p, pd);
				dtoList.add(pd);
			}
		}
		return dtoList;
	}

}