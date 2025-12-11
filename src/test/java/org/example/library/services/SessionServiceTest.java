package org.example.library.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SessionServiceTest {

    @Test
    void testInitialState_isLoggedOut() {
        SessionService session = new SessionService();
        assertFalse(session.isLoggedIn(), "Default state should be logged out");
    }

    @Test
    void testLogin_changesStateToLoggedIn() {
        SessionService session = new SessionService();
        session.login();
        assertTrue(session.isLoggedIn(), "User should be logged in after login()");
    }

    @Test
    void testLogout_changesStateToLoggedOut() {
        SessionService session = new SessionService();
        session.login();   // login first
        session.logout();  // logout
        assertFalse(session.isLoggedIn(), "User should be logged out after logout()");
    }

    @Test
    void testMultipleLoginCallsStillLoggedIn() {
        SessionService session = new SessionService();
        session.login();
        session.login(); // duplicate call
        assertTrue(session.isLoggedIn());
    }

    @Test
    void testMultipleLogoutCallsStillLoggedOut() {
        SessionService session = new SessionService();
        session.logout();
        session.logout(); // duplicate call
        assertFalse(session.isLoggedIn());
    }
}
