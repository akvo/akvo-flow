/*
 *  Copyright (C) 2014,2017 Stichting Akvo (Akvo Foundation)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.RestStatusDto;
import org.waterforpeople.mapping.app.web.rest.dto.UserRolePayload;

import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.Permission;
import com.gallatinsystems.user.domain.UserRole;

@Controller
@RequestMapping("/user_roles")
public class UserRolesRestService {

    private UserRoleDao userRoleDao = new UserRoleDao();

    private UserAuthorizationDAO userAuthorizationDAO = new UserAuthorizationDAO();

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> createUserRole(@RequestBody UserRolePayload payload) {
        final RestStatusDto statusDto = new RestStatusDto();

        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("meta", statusDto);

        if (StringUtils.isBlank(payload.getName())) {
            statusDto.setMessage("_missing_role_name");
            return response;
        }

        if (userRoleDao.findUserRoleByName(payload.getName()) == null) {
            UserRole createdRole = userRoleDao.save(payload.getUserRole());
            statusDto.setStatus("ok");
            statusDto.setMessage("_role_created");
            response.put("user_roles", new UserRolePayload(createdRole));
        } else {
            statusDto.setMessage("_role_already_exists");
        }
        return response;
    }

    /**
     * Retrieve the list of all user roles defined
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listUserRoles() {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<UserRolePayload> rolesPayload = new ArrayList<UserRolePayload>();
        for (UserRole role : userRoleDao.listAllRoles()) {
            rolesPayload.add(new UserRolePayload(role));
        }
        response.put("user_roles", rolesPayload);
        return response;
    }

    /**
     * Retrieve a role by its id.
     *
     * @param roleId
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{roleId}")
    @ResponseBody
    public Map<String, Object> findUserRole(@PathVariable Long roleId) {
        final Map<String, Object> response = new HashMap<String, Object>();
        RestStatusDto statusDto = new RestStatusDto();
        response.put("meta", statusDto);

        UserRole role = userRoleDao.getByKey(roleId);
        if (role == null) {
            statusDto.setMessage("_role_not_found");
            return response;
        }
        statusDto.setStatus("ok");
        response.put("user_roles", new UserRolePayload(role));
        return response;
    }

    /**
     * Update an existing user role
     *
     * @param payload
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{roleId}")
    @ResponseBody
    public Map<String, Object> updateUserRole(@PathVariable Long roleId,
            @RequestBody UserRolePayload payload) {
        final RestStatusDto statusDto = new RestStatusDto();
        statusDto.setStatus("failed");

        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("meta", statusDto);

        if (StringUtils.isBlank(payload.getName())) {
            statusDto.setMessage("_missing_role_name");
            return response;
        }

        UserRole existingRole = userRoleDao.getByKey(roleId);
        if (existingRole == null) {
            statusDto.setMessage("_role_not_found");
            return response;
        }

        if (!existingRole.getName().equals(payload.getName())) {
            UserRole duplicateRoleName = userRoleDao.findUserRoleByName(payload.getName());
            if (duplicateRoleName != null) {
                statusDto.setMessage("_duplicate_role_name");
                return response;
            }
        }

        BeanUtils.copyProperties(payload, existingRole);

        UserRolePayload updatedRole = new UserRolePayload(userRoleDao.save(existingRole));
        response.put("user_roles", updatedRole);
        statusDto.setStatus("ok");

        return response;
    }

    /**
     * Delete a user role definition
     *
     * @param roleId
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{roleId}")
    @ResponseBody
    public Map<String, Object> deleteUserRole(@PathVariable Long roleId) {
        final RestStatusDto statusDto = new RestStatusDto();

        final Map<String, Object> response = new HashMap<String, Object>();
        response.put("meta", statusDto);

        UserRole deleteRole = userRoleDao.getByKey(roleId);
        if (deleteRole == null) {
            statusDto.setStatus("ok");
            statusDto.setMessage("_role_not_found");
            return response;
        }

        if (userAuthorizationDAO.findFirstAssignedByRole(roleId).isEmpty()) {
            userRoleDao.delete(deleteRole);
            statusDto.setStatus("ok");
            statusDto.setMessage("_role_deleted");
        } else {
            statusDto.setMessage("_role_in_use");
        }

        return response;
    }

    /**
     * Return a list of all system permissions
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/permissions")
    @ResponseBody
    public Map<String, Object> listAllPermissions() {
        final Map<String, Object> response = new HashMap<String, Object>();
        Set<Permission> permissions = new HashSet<Permission>(Arrays.asList(Permission.values()));
        response.put("permissions", permissions);
        return response;
    }
}
