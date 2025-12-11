package org.example.library.services;

import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Test
    void getAllUsers_returnsCorrectList() {

        // Fake database JSON
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject u1 = new JSONObject();
        u1.put("id", 1);
        u1.put("username", "dima");
        u1.put("password", "123");
        u1.put("role", "admin");
        u1.put("email", "dima@mail.com");

        JSONObject u2 = new JSONObject();
        u2.put("id", 2);
        u2.put("username", "sara");
        u2.put("password", "pass");
        u2.put("role", "user");
        u2.put("email", "sara@mail.com");

        arr.put(u1);
        arr.put(u2);

        db.put("users", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            // Mock load() to return our fake DB
            mock.when(FileDatabase::load).thenReturn(db);

            UserService service = new UserService();

            List<User> result = service.getAllUsers();

            assertEquals(2, result.size());

            User first = result.get(0);
            assertEquals(1, first.getId());
            assertEquals("dima", first.getUsername());
            assertEquals("admin", first.getRole());
            assertEquals("dima@mail.com", first.getEmail());

            User second = result.get(1);
            assertEquals(2, second.getId());
            assertEquals("sara", second.getUsername());
            assertEquals("sara@mail.com", second.getEmail());
        }
    }

    // ============================================================
    // Empty users array → should return empty list
    // ============================================================
    @Test
    void getAllUsers_emptyList() {

        JSONObject db = new JSONObject();
        db.put("users", new JSONArray()); // empty array

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            UserService service = new UserService();
            List<User> result = service.getAllUsers();

            assertTrue(result.isEmpty());
        }
    }
}
