/*
 *  Copyright (C) 2022 Stichting Akvo (Akvo Foundation)
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

 *  The full license text can also be seen at <http://www.gnu.org/licenses/agpl.html>.
 */

package org.akvo.flow.servlet;

import com.gallatinsystems.common.util.PropertyUtil;
import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserAuthorization;
import org.akvo.flow.domain.DefaultUserAuthorization;
import org.akvo.flow.domain.RootFolder;
import org.akvo.flow.rest.dto.PostUserRegistrationRestRequest;
import org.akvo.flow.rest.security.AppRole;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

import static org.waterforpeople.mapping.app.web.EnvServlet.SELF_ONBOARD_ENABLED;

public class PostUserRegistrationServlet extends AbstractRestApiServlet {
    private UserDao userDao = new UserDao();

    private SurveyGroupDAO folderDao = new SurveyGroupDAO();

    @Override
    protected RestRequest convertRequest() throws Exception {
        HttpServletRequest req = getRequest();
        RestRequest restRequest = new PostUserRegistrationRestRequest();
        restRequest.populateFromHttpRequest(req);
        return restRequest;

    }

    @Override
    protected RestResponse handleRequest(RestRequest req) throws Exception {
        RestResponse resp = new RestResponse();
        PostUserRegistrationRestRequest request = (PostUserRegistrationRestRequest) req;
        if (!"true".equalsIgnoreCase(PropertyUtil.getProperty(SELF_ONBOARD_ENABLED))) {
            resp.setMessage("Not valid self onboarding instance");
            return resp;
        }

        User newUser = addUser(request.getEmail(), request.getUserName());
        SurveyGroup folder = addFolder(newUser.getUserName(), newUser.getKey().getId());
        addAuthorization(newUser, folder);

        resp.setMessage("Registration completed");
        return resp;
    }

    private User addUser(String email, String userName) {
        User newUser = userDao.findUserByEmail(email);
        if (newUser != null) {
            return newUser;
        }
        newUser = new User();
        newUser.setEmailAddress(email);
        newUser.setUserName(userName);
        newUser.setPermissionList(Integer.toString(AppRole.ROLE_USER.getLevel()));
        newUser.setSuperAdmin(false);

        User saved = userDao.save(newUser);

        return saved;
    }

    private SurveyGroup addFolder(String folderName, Long creationUserId) {
        SurveyGroup folder = folderDao.findBySurveyGroupName(folderName);
        if (folder != null) {
            return folder;
        }

        folder = new SurveyGroup();
        folder.setProjectType(SurveyGroup.ProjectType.PROJECT_FOLDER);
        folder.setName(folderName);
        folder.setCode(folderName);
        folder.setCreateUserId(creationUserId);
        folder.setParentId(new RootFolder().getObjectId());
        folder.setPath("/" + folder.getCode());
        folder.setAncestorIds(Arrays.asList(0L));

        SurveyGroup saved = folderDao.save(folder);

        return saved;
    }

    private void addAuthorization(User newUser, SurveyGroup folder) {
        UserAuthorization authorization = DefaultUserAuthorization
                .getOrCreateDefaultAuthorization(newUser.getKey().getId(), folder.getKey().getId(), folder.getName());
        new UserAuthorizationDAO().save(authorization);
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {

    }
}
