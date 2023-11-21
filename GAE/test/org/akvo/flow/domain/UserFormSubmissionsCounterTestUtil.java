package org.akvo.flow.domain;

import com.gallatinsystems.user.dao.UserDao;
import com.gallatinsystems.user.domain.User;
import com.gallatinsystems.survey.dao.SurveyDAO;
import com.gallatinsystems.survey.dao.SurveyGroupDAO;
import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;

import java.util.Date;

import org.akvo.flow.rest.security.AppRole;
import org.waterforpeople.mapping.dao.SurveyInstanceDAO;
import org.waterforpeople.mapping.domain.SurveyInstance;

class UserFormSubmissionsCounterTestUtil {
    private final UserDao userDao = new UserDao();
    private final SurveyGroupDAO surveyGroupDAO = new SurveyGroupDAO();
    private final SurveyDAO surveyDAO = new SurveyDAO();
    private final SurveyInstanceDAO surveyInstanceDAO = new SurveyInstanceDAO();

    public User createUser(final String email) {
        final User user = new User();
        user.setEmailAddress(email);
        user.setUserName(email);
        user.setPermissionList(Integer.toString(AppRole.ROLE_USER.getLevel()));
        user.setSuperAdmin(false);

        return userDao.save(user);
    }

    public SurveyGroup createFolder() {
        return createFolder(0L);
    }

    public SurveyGroup createFolder(final SurveyGroup parent) {
        return createFolder(parent.getKey().getId());
    }

    public SurveyGroup createFolder(final Long parentId) {
        final SurveyGroup folder = new SurveyGroup();
        folder.setParentId(parentId);
        return surveyGroupDAO.save(folder);
    }

    public Survey createSurvey(SurveyGroup folder, User creator) {
        final Survey survey = new Survey();
        survey.setSurveyGroupId(folder.getKey().getId());
        survey.setCreateUserId(creator.getKey().getId());

        // hack to force setCreateUserId
        survey.setLastUpdateDateTime(new Date());
        survey.setCreatedDateTime(survey.getLastUpdateDateTime());

        return surveyDAO.save(survey);
    }

    public SurveyInstance createSurveyInstance(Survey survey) {
        final SurveyInstance obj = new SurveyInstance();
        obj.setSurveyId(survey.getKey().getId());
        return surveyInstanceDAO.save(obj);
    }
}
