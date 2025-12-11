package org.example.library.services;

import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    private AdminService adminService;

    @BeforeEach
    void setup() {
        adminService = new AdminService();
    }

    // ===============================
    //            LOGIN TESTS
    // ===============================

    @Test
    void login_success() {
        JSONObject fakeDB = new JSONObject();
        JSONArray users = new JSONArray();

        JSONObject user = new JSONObject();
        user.put("id", 3);
        user.put("username", "dima");
        user.put("password", "1234");
        user.put("role", "admin");
        user.put("email", "dima@mail.com");

        users.put(user);
        fakeDB.put("users", users);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            User result = adminService.login("dima", "1234");

            assertNotNull(result);
            assertEquals("dima", result.getUsername());
            assertEquals("admin", result.getRole());
        }
    }

    @Test
    void login_wrongPassword_returnsNull() {
        JSONObject fakeDB = new JSONObject();
        JSONArray users = new JSONArray();

        JSONObject user = new JSONObject();
        user.put("username", "dima");
        user.put("password", "1234");
        user.put("role", "admin");

        users.put(user);
        fakeDB.put("users", users);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            User result = adminService.login("dima", "wrong");
            assertNull(result);
        }
    }

    @Test
    void login_userNotFound_returnsNull() {
        JSONObject fakeDB = new JSONObject();
        fakeDB.put("users", new JSONArray());

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            User result = adminService.login("x", "y");
            assertNull(result);
        }
    }

    // ===============================
    //       UNREGISTER USER TESTS
    // ===============================

    @Test
    void unregister_nonAdmin_cannotRemove() {
        User notAdmin = new User(5, "user", "123", "user", "u@mail.com");

        boolean result = adminService.unregisterUser("someone", notAdmin);

        assertFalse(result);
    }

    @Test
    void unregister_userNotFound() {
        JSONObject fakeDB = new JSONObject();
        fakeDB.put("users", new JSONArray());
        fakeDB.put("books", new JSONArray());

        User admin = new User(1, "root", "x", "admin", "a@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            boolean result = adminService.unregisterUser("ghost", admin);
            assertFalse(result);
        }
    }

    @Test
    void unregister_adminCannotBeDeleted() {
        JSONObject fakeDB = new JSONObject();

        JSONArray users = new JSONArray();
        JSONObject adminUser = new JSONObject();
        adminUser.put("username", "super");
        adminUser.put("role", "admin");
        users.put(adminUser);

        fakeDB.put("users", users);
        fakeDB.put("books", new JSONArray());

        User admin = new User(1, "root", "x", "admin", "root@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            boolean result = adminService.unregisterUser("super", admin);
            assertFalse(result);
        }
    }

    @Test
    void unregister_userHasActiveLoans_fails() {
        JSONObject fakeDB = new JSONObject();

        JSONArray users = new JSONArray();
        JSONObject u = new JSONObject();
        u.put("username", "sara");
        u.put("role", "user");
        u.put("id", 10);
        users.put(u);

        JSONArray books = new JSONArray();
        JSONObject b = new JSONObject();
        b.put("borrowedBy", 10);
        b.put("borrowed", true);
        books.put(b);

        fakeDB.put("users", users);
        fakeDB.put("books", books);

        User admin = new User(1, "root", "x", "admin", "root@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            boolean result = adminService.unregisterUser("sara", admin);
            assertFalse(result);
        }
    }

    @Test
    void unregister_successfullyRemovesUser() {
        JSONObject fakeDB = new JSONObject();

        JSONArray users = new JSONArray();
        JSONObject u = new JSONObject();
        u.put("username", "sara");
        u.put("role", "user");
        u.put("id", 10);
        users.put(u);

        JSONArray books = new JSONArray(); // no loans

        fakeDB.put("users", users);
        fakeDB.put("books", books);

        User admin = new User(1, "root", "x", "admin", "root@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);
            mock.when(() -> FileDatabase.save(fakeDB)).thenAnswer(inv -> null);

            boolean result = adminService.unregisterUser("sara", admin);

            assertTrue(result);
            assertEquals(0, fakeDB.getJSONArray("users").length());
        }
    }


    @Test
    void unregister_userHasUnpaidFines_fails() {
        JSONObject fakeDB = new JSONObject();

        JSONArray users = new JSONArray();
        JSONObject u = new JSONObject();
        u.put("username", "sara");
        u.put("role", "user");
        u.put("id", 10);
        users.put(u);

        JSONArray books = new JSONArray();
        JSONObject b = new JSONObject();
        b.put("borrowedBy", 10);
        b.put("borrowed", false);
        b.put("fine", 6.0);
        books.put(b);

        fakeDB.put("users", users);
        fakeDB.put("books", books);

        User admin = new User(1, "root", "x", "admin", "root@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            boolean result = adminService.unregisterUser("sara", admin);

            assertFalse(result);
        }
    }

    @Test
    void unregister_userWithoutId_succeeds() {
        JSONObject fakeDB = new JSONObject();

        JSONArray users = new JSONArray();
        JSONObject u = new JSONObject();
        u.put("username", "dima-no-id");
        u.put("role", "user");
        users.put(u);  // ⭐ no ID field

        JSONArray books = new JSONArray(); // irrelevant

        fakeDB.put("users", users);
        fakeDB.put("books", books);

        User admin = new User(1, "root", "x", "admin", "root@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);
            mock.when(() -> FileDatabase.save(fakeDB)).thenAnswer(inv -> null);

            boolean result = adminService.unregisterUser("dima-no-id", admin);

            assertTrue(result);
        }
    }


    @Test
    void unregister_middleUserRemovedCorrectly() {
        JSONObject fakeDB = new JSONObject();

        JSONArray users = new JSONArray();

        JSONObject u1 = new JSONObject();
        u1.put("username", "first");
        u1.put("role", "user");
        u1.put("id", 1);

        JSONObject u2 = new JSONObject();
        u2.put("username", "middle");
        u2.put("role", "user");
        u2.put("id", 2);

        JSONObject u3 = new JSONObject();
        u3.put("username", "last");
        u3.put("role", "user");
        u3.put("id", 3);

        users.put(u1);
        users.put(u2);
        users.put(u3);

        fakeDB.put("users", users);
        fakeDB.put("books", new JSONArray());

        User admin = new User(100, "root", "x", "admin", "root@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);
            mock.when(() -> FileDatabase.save(fakeDB)).thenAnswer(inv -> null);

            boolean result = adminService.unregisterUser("middle", admin);

            assertTrue(result);
            assertEquals(2, fakeDB.getJSONArray("users").length());
            assertEquals("first", fakeDB.getJSONArray("users").getJSONObject(0).getString("username"));
            assertEquals("last", fakeDB.getJSONArray("users").getJSONObject(1).getString("username"));
        }
    }



}
