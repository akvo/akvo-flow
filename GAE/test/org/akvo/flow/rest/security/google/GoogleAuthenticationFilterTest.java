package org.akvo.flow.rest.security.google;

import org.akvo.flow.rest.security.GaeUserAuthentication;
import org.akvo.flow.rest.security.user.GaeUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;

import com.google.appengine.api.users.User;

import static org.junit.jupiter.api.Assertions.*;

class GoogleAuthenticationFilterTest {

    private static final User ANY_GOOGLE_USER = googleUser("any@foo.bar");

    @Test
    void notLoggedIn() {
        Authentication notLoggedIn = null;
        assertDoNotClearContext(notLoggedIn, ANY_GOOGLE_USER);
    }

    @Test
    void loggedInWithAuth0() {
        assertDoNotClearContext(auth0User(), ANY_GOOGLE_USER);
    }

    @Test
    void userLoggedOutFromGoogleButNotFromFlow() {
        User notLoggedInGoogle = null;
        assertClearSecurityContext(gaeUser("any@akvo.org"), notLoggedInGoogle);
    }

    @Test
    void userLoggedInGoogleWithADifferentUserThanFlow() {
        assertClearSecurityContext(gaeUser("this.one@akvo.org"), googleUser("this.other.one@akvo.org"));
    }

    @Test
    void userLoggedInGoogleWithSameUserAsFlow() {
        String sameEmail = "same@akvo.org";
        assertDoNotClearContext(gaeUser(sameEmail), googleUser(sameEmail));
    }

    private static User googleUser(String email) {
        return new User(email, "any");
    }

    private void assertClearSecurityContext(Authentication authentication, User googleUser) {
        assertTrue(GoogleAuthenticationFilter.shouldClearSession(authentication, googleUser));
    }

    private Authentication auth0User() {
        return new GaeUserAuthentication(new GaeUser(false, "any", "any"), null);
    }

    private Authentication gaeUser(String email) {
        return new GaeUserAuthentication(new GaeUser(true, "any", email), null);
    }

    private void assertDoNotClearContext(Authentication notLoggedIn, User anyGoogleUser) {
        assertFalse(GoogleAuthenticationFilter.shouldClearSession(notLoggedIn, anyGoogleUser));
    }

}