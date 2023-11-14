package org.akvo.flow.domain;

import com.gallatinsystems.survey.domain.Survey;
import com.gallatinsystems.survey.domain.SurveyGroup;
import com.gallatinsystems.user.domain.User;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserFormSubmissionsCounterTest {
    private final LocalServiceTestHelper helper = new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());
    private final UserFormSubmissionsCounterTestUtil util = new UserFormSubmissionsCounterTestUtil();

    @BeforeEach
    public void setUp() {
        helper.setUp();
    }

    @AfterEach
    public void tearDown() {
        helper.tearDown();
    }

    @Test
    void testNoSurveys() {
        UserFormSubmissionsCounter counter = createSUT();
        User user = util.createUser("user1@akvo.org");

        assertEquals(0, counter.countFor(user));
    }

    @Test
    void testNoSubmissions() {
        UserFormSubmissionsCounter counter = createSUT();
        User user = util.createUser("user1@akvo.org");
        SurveyGroup folder = util.createFolder();
        util.createSurvey(folder, user);

        assertEquals(0, counter.countFor(user));
    }

    @Test
    void testCountFormSubmissionOfAUserAccount()  {
        UserFormSubmissionsCounter counter = createSUT();
        User user1 = setupUser1();
        User user2 = setupUser2();

        assertEquals(3, counter.countFor(user1));
        assertEquals(2, counter.countFor(user2));
    }

    private UserFormSubmissionsCounter createSUT() {
        return new UserFormSubmissionsCounter(DatastoreServiceFactory.getDatastoreService());
    }

    private User setupUser1() {
        User user = util.createUser("user1@akvo.org");

        SurveyGroup level0Folder = util.createFolder();
        SurveyGroup level1Folder = util.createFolder(level0Folder);
        SurveyGroup level2Folder = util.createFolder(level1Folder);

        Survey level1Survey = util.createSurvey(level1Folder, user);
        Survey level2Survey = util.createSurvey(level2Folder, user);

        util.createSurveyInstance(level1Survey);
        util.createSurveyInstance(level2Survey);
        util.createSurveyInstance(level2Survey);

        System.out.println("user1 level1Survey: " + level1Survey.getKey().getId() + ", createUserId: " + level1Survey.getCreateUserId());
        System.out.println("user1 level2Survey: " + level2Survey.getKey().getId() + ", createUserId: " + level2Survey.getCreateUserId());

        return user;
    }

    private User setupUser2() {
        User user = util.createUser("user2@akvo.org");
        SurveyGroup level0Folder = util.createFolder();
        Survey level0Survey = util.createSurvey(level0Folder, user);
        util.createSurveyInstance(level0Survey);
        util.createSurveyInstance(level0Survey);

        System.out.println("user2 level1Survey: " + level0Survey.getKey().getId() + ", createUserId: " + level0Survey.getCreateUserId());

        return user;
    }
}
