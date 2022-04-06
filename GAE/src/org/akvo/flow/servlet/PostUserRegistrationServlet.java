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

import com.gallatinsystems.framework.rest.AbstractRestApiServlet;
import com.gallatinsystems.framework.rest.RestRequest;
import com.gallatinsystems.framework.rest.RestResponse;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.user.dao.UserAuthorizationDAO;
import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.dao.UserRoleDao;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.user.domain.UserAuthorization;
import com.gallatinsystems.user.domain.UserRole;
import org.akvo.flow.domain.DefaultUserAuthorization;
import org.akvo.flow.rest.dto.PostUserRegistrationRestRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;

public class PostUserRegistrationServlet extends AbstractRestApiServlet {
    private final String MAIN_DOMAIN = "www";

    private final String DEMO_DOMAIN = "demo";

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
        PostUserRegistrationRestRequest request = (PostUserRegistrationRestRequest) req;
        if (!MAIN_DOMAIN.equalsIgnoreCase(request.getDomain()) || !DEMO_DOMAIN.equalsIgnoreCase(request.getDomain())) {
            return new RestResponse();
        }

        User newUser = addUser(request.getEmail(), request.getFullName());
        SurveyGroup folder = addFolder(newUser.getUserName(), newUser.getKey().getId());
        addAuthorization(newUser, folder);

        return new RestResponse();
    }

    private User addUser(String email, String fullName) {
        User newUser = new User();
        newUser.setEmailAddress(email);
        newUser.setUserName(fullName);

        return userDao.save(newUser);
    }

    private SurveyGroup addFolder(String folderName, Long creationUserId) {
        SurveyGroup folder = new SurveyGroup();
        folder.setProjectType(SurveyGroup.ProjectType.PROJECT_FOLDER);
        folder.setName(folderName);
        folder.setCode(folderName);
        folder.setCreateUserId(creationUserId);

        return folderDao.save(folder);
    }

    private void addAuthorization(User newUser, SurveyGroup folder) {
        UserAuthorization authorization = DefaultUserAuthorization
                .getOrCreateDefaultAuthorization(newUser.getKey().getId(), folder.getKey().getId());
        new UserAuthorizationDAO().save(authorization);
    }

    @Override
    protected void writeOkResponse(RestResponse resp) throws Exception {

    }
}
