/*
 *  Copyright (C) 2012-2014,2017 Stichting Akvo (Akvo Foundation)
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

package org.waterforpeople.mapping.app.web.rest;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.CurrentUserServlet;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.UserPayload;
import org.waterforpeople.mapping.app.web.rest.security.AppRole;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.user.app.gwt.client.UserDto;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;

@Controller
@RequestMapping("/users")
public class UserRestService {

    private UserDao userDao = new UserDao();

    // TODO put in meta information?
    // list all users
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listUsers(
            @RequestParam(value = "currUser", defaultValue = "false") String returnOnlyCurrentUser) {
        final Map<String, Object> response = new HashMap<String, Object>();
        final List<UserDto> results = new ArrayList<UserDto>();
        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("");
        statusDto.setMessage("");

        response.put("users", results);
        response.put("meta", statusDto);

        User currentUser = CurrentUserServlet.getCurrentUser();
        if (!isSuperAdminRole(currentUser)) {
            UserDto currentUserDto = new UserDto();
            BeanUtils.copyProperties(currentUser, currentUserDto, new String[] {
                    "config"
            });
            if (currentUser.getKey() != null) {
                currentUserDto.setKeyId(currentUser.getKey().getId());
            }
            results.add(currentUserDto);
        }

        if ("true".equals(returnOnlyCurrentUser) || !isUserAdminRole(currentUser)) {
            return response;
        }

        // rest of the users
        List<User> users = userDao.list(Constants.ALL_RESULTS);
        if (users != null) {
            for (User u : users) {
                // skip super users + current user is already in list
                if (u.getKey().equals(currentUser.getKey()) || isSuperAdminRole(u)) {
                    continue;
                }

                UserDto dto = new UserDto();
                BeanUtils.copyProperties(u, dto, new String[] {
                        "config"
                });
                if (u.getKey() != null) {
                    dto.setKeyId(u.getKey().getId());
                }
                results.add(dto);
            }
        }
        return response;
    }

    private boolean isUserAdminRole(User user) {
        return user.getPermissionList().equals(Integer.toString(AppRole.ADMIN.getLevel()))
                || user.getPermissionList()
                        .equals(Integer.toString(AppRole.SUPER_ADMIN.getLevel()));
    }

    private boolean isSuperAdminRole(User user) {
        return user.isSuperAdmin()
                || user.getPermissionList()
                        .equals(Integer.toString(AppRole.SUPER_ADMIN.getLevel()));
    }

    // find a single user by the userId
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @ResponseBody
    public Map<String, UserDto> findUser(@PathVariable("id") Long id) {
        final Map<String, UserDto> response = new HashMap<String, UserDto>();
        User u = userDao.getByKey(id);
        UserDto dto = null;
        if (u != null) {
            dto = new UserDto();
            BeanUtils.copyProperties(u, dto, new String[] {
                    "config"
            });
            if (u.getKey() != null) {
                dto.setKeyId(u.getKey().getId());
            }
        }
        response.put("user", dto);
        return response;

    }

    // delete user by id
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
    @ResponseBody
    public Map<String, RestStatusDto> deleteUserById(@PathVariable("id") Long id) {
        final Map<String, RestStatusDto> response = new HashMap<String, RestStatusDto>();
        User u = userDao.getByKey(id);
        RestStatusDto statusDto = null;
        statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // check if user exists in the datastore
        if (u != null) {
            // delete user group
            userDao.delete(u);
            statusDto.setStatus("ok");
        }
        response.put("meta", statusDto);
        return response;
    }

    // update existing user
    @RequestMapping(method = RequestMethod.PUT, value = "/{id}")
    @ResponseBody
    public Map<String, Object> saveExistingUser(@RequestBody UserPayload payLoad) {
        final UserDto userDto = payLoad.getUser();
        final Map<String, Object> response = new HashMap<String, Object>();
        UserDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid userDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (userDto != null) {
            Long keyId = userDto.getKeyId();
            User u;

            // if the userDto has a key, try to get the user.
            if (keyId != null) {
                u = userDao.getByKey(keyId);
                // if we find the user, update it's properties
                if (u != null) {
                    // copy the properties, except the createdDateTime property,
                    // because it is set in the Dao.
                    BeanUtils.copyProperties(userDto, u, new String[] {
                            "createdDateTime", "config"
                    });

                    if (u.getPermissionList().equals(
                            String.valueOf(AppRole.SUPER_ADMIN.getLevel()))) {
                        u.setPermissionList(String.valueOf(AppRole.USER
                                .getLevel()));
                    }

                    u = userDao.save(u);
                    dto = new UserDto();
                    BeanUtils.copyProperties(u, dto, new String[] {
                            "config"
                    });
                    if (u.getKey() != null) {
                        dto.setKeyId(u.getKey().getId());
                    }
                    statusDto.setStatus("ok");
                }
            }
        }
        response.put("meta", statusDto);
        response.put("user", dto);
        return response;
    }

    // create new user
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> saveNewUser(@RequestBody UserPayload payLoad) {
        final UserDto userDto = payLoad.getUser();
        final Map<String, Object> response = new HashMap<String, Object>();
        UserDto dto = null;

        RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        // if the POST data contains a valid userDto, continue.
        // Otherwise, server will respond with 400 Bad Request
        if (userDto != null) {
            User u = new User();

            // copy the properties, except the createdDateTime property, because
            // it is set in the Dao.
            BeanUtils.copyProperties(userDto, u, new String[] {
                    "createdDateTime", "config"
            });

            if (u.getPermissionList().equals(
                    String.valueOf(AppRole.SUPER_ADMIN.getLevel()))) {
                u.setPermissionList(String.valueOf(AppRole.USER.getLevel()));
            }

            u = userDao.save(u);

            dto = new UserDto();
            BeanUtils.copyProperties(u, dto, new String[] {
                    "config"
            });
            if (u.getKey() != null) {
                dto.setKeyId(u.getKey().getId());
            }
            statusDto.setStatus("ok");
        }

        response.put("meta", statusDto);
        response.put("user", dto);
        return response;
    }

    // Create new API keys
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/apikeys")
    @ResponseBody
    public Map<String, Map<String, String>> createApiKeys(@PathVariable("id") Long id) {

        final Map<String, Map<String, String>> response = new HashMap<String, Map<String, String>>();
        Map<String, String> result = new HashMap<String, String>();

        User user = userDao.getByKey(id);

        if (user == null) {
            throw new ResourceNotFoundException();
        }
        String accessKey = createRandomKey();
        String secret = createRandomKey();
        user.setAccessKey(accessKey);
        user.setSecret(secret);
        userDao.save(user);
        result.put("secret", secret);
        result.put("accessKey", accessKey);
        response.put("apikeys", result);
        return response;
    }

    // Delete existing API keys
    @RequestMapping(method = RequestMethod.DELETE, value = "/{id}/apikeys")
    @ResponseBody
    public Map<String, String> deleteApiKeys(@PathVariable("id") Long id) {

        final Map<String, String> response = new HashMap<String, String>();

        User user = userDao.getByKey(id);

        if (user == null) {
            throw new ResourceNotFoundException();
        }
        user.setAccessKey(null);
        user.setSecret(null);
        userDao.save(user);
        response.put("apikeys", "deleted");
        return response;
    }

    static String createRandomKey() {
        SecureRandom secureRandom = new SecureRandom();
        byte bytes[] = new byte[32];
        secureRandom.nextBytes(bytes);
        return Base64.encodeBase64String(bytes).trim();
    }
}
