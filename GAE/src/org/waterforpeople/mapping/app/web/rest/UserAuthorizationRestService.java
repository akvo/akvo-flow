/*
 *  Copyright (C) 2014 Stichting Akvo (Akvo Foundation)
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
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

    @Inject
    private UserAuthorizationDAO userAuthorizationDAO;

    @ResponseBody
    public Map<String, Object> listUserAuthorization() {
    }

    @ResponseBody
    public Map<String, Object> findUserAuthorization(Long userId) {
    }

    @RequestMapping(method = RequestMethod.POST, value = "")
    @ResponseBody
    public Map<String, Object> createUserAuthorization(
            @RequestBody UserAuthorizationPayload requestPayload) {

        final Map<String, Object> response = new HashMap<String, Object>();
        final UserAuthorization newAuth = requestPayload.getUserAuthorisation();
        final UserAuthorization existingAuth = userAuthorizationDAO.findUserAuthorization(
                newAuth.getUserId(), newAuth.getRoleId(), newAuth.getObjectPath());

        UserAuthorizationPayload responsePayload = null;
        if (existingAuth != null) {
            responsePayload = new UserAuthorizationPayload(existingAuth);
        } else {
            responsePayload = new UserAuthorizationPayload(userAuthorizationDAO.save(newAuth));
        }
        response.put("user_auth", responsePayload);
        return response;
    }

    public Map<String, Object> updateUserAuthorization() {

    }

    public Map<String, Object> deleteUserAuthorization() {

    }
}
