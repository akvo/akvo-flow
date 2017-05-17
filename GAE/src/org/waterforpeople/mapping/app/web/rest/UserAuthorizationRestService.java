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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.waterforpeople.mapping.app.web.rest.dto.UserAuthorizationPayload;

import com.gallatinsystems.common.Constants;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.domain.UserAuthorization;

@Controller
@RequestMapping("/user_auth")
public class UserAuthorizationRestService {

    private UserAuthorizationDAO userAuthorizationDAO = new UserAuthorizationDAO();

    /**
     * List the authorization parameters for all users
     *
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "")
    @ResponseBody
    public Map<String, Object> listAllUserAuthorizations() {
        final Map<String, Object> response = new HashMap<String, Object>();
        List<UserAuthorization> authorizationList = userAuthorizationDAO
                .list(Constants.ALL_RESULTS);
        List<UserAuthorizationPayload> responsePayloadList = new ArrayList<UserAuthorizationPayload>();
        for (UserAuthorization auth : authorizationList) {
            responsePayloadList.add(new UserAuthorizationPayload(auth));
        }
        response.put("user_auth", responsePayloadList);
        return response;
    }

    /**
     * Create a new UserAuthorization from posted payload. If an authorization with the same
     * parameters already exists, it is returned instead.
     *
     * @param requestPayload
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> createUserAuthorization(
            @RequestBody UserAuthorizationPayload requestPayload) {

        final Map<String, Object> response = new HashMap<String, Object>();
        final UserAuthorization newAuth = requestPayload.getUserAuthorisation();
        final UserAuthorization existingAuth = userAuthorizationDAO.findUserAuthorization(
                newAuth.getUserId(), newAuth.getRoleId(), newAuth.getSecuredObjectId());

        UserAuthorizationPayload responsePayload = null;
        if (existingAuth != null) {
            responsePayload = new UserAuthorizationPayload(existingAuth);
        } else {
            responsePayload = new UserAuthorizationPayload(userAuthorizationDAO.save(newAuth));
        }
        response.put("user_auth", responsePayload);
        return response;
    }

    /**
     * Update an authorization definition. This is restricted to updating the role and the path. The
     * user for an authorization cannot be changed.
     *
     * @param requestPayload
     * @param authId
     * @return
     */
    @RequestMapping(method = RequestMethod.PUT, value = "/{authId}")
    @ResponseBody
    public Map<String, Object> updateUserAuthorization(
            @RequestBody UserAuthorizationPayload requestPayload, @PathVariable Long authId) {

        final Map<String, Object> response = new HashMap<String, Object>();
        final UserAuthorization existingAuth = userAuthorizationDAO.getByKey(authId);

        if (existingAuth != null) {
            BeanUtils.copyProperties(requestPayload, existingAuth,
                    new String[] {
                        "userId"
                    }); // we should not switch the user on an authorization.
            response.put("user_auth", new UserAuthorizationPayload(existingAuth));
        }
        return response;
    }

    /**
     * Delete an authorization definition.
     *
     * @param authId
     */
    @RequestMapping(method = RequestMethod.DELETE, value = "/{authId}")
    @ResponseBody
    public void deleteUserAuthorization(@PathVariable Long authId) {
        final UserAuthorization existingAuth = userAuthorizationDAO.getByKey(authId);
        userAuthorizationDAO.delete(existingAuth);
    }
}
