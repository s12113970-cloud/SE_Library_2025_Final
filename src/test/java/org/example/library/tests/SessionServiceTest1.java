package org.example.library.tests;

import org.example.library.services.SessionService;
import org.example.library.storage.FileDatabase;
import org.junit.jupiter.api.*;

class SessionServiceTest1 {

    @BeforeAll
    static void useTestDB() {
        FileDatabase.useTestDatabase();
    }

    @Test
    void testLogout() {
        SessionService s = new SessionService();
        s.login();
        Assertions.assertTrue(s.isLoggedIn());

        s.logout();
        Assertions.assertFalse(s.isLoggedIn());
    }
}
