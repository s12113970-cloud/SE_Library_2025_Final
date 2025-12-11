package org.example.library.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testUserConstructorAndGetters() {
        User user = new User(1, "dima", "1234", "admin", "dima@gmail.com");

        assertEquals(1, user.getId());
        assertEquals("dima", user.getUsername());
        assertEquals("1234", user.getPassword());
        assertEquals("admin", user.getRole());
        assertEquals("dima@gmail.com", user.getEmail());
    }

    @Test
    void testCheckPasswordCorrect() {
        User user = new User(2, "sara", "pass123", "user", "sara@gmail.com");

        assertTrue(user.checkPassword("pass123"),
                "Password comparison should return true when matching");
    }

    @Test
    void testCheckPasswordIncorrect() {
        User user = new User(3, "ahmad", "abcd", "user", "ahmad@gmail.com");

        assertFalse(user.checkPassword("wrong"),
                "Password comparison should return false when incorrect");
    }

    @Test
    void testIsAdminTrue() {
        User user = new User(10, "adminUser", "pass", "admin", "a@a.com");

        assertTrue(user.isAdmin(), "User with role 'admin' should be recognized as admin");
    }

    @Test
    void testIsAdminFalse() {
        User user = new User(11, "notAdmin", "pass", "user", "u@u.com");

        assertFalse(user.isAdmin(), "User with role 'user' should not be admin");
    }
}
