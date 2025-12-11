package org.example.library.tests;

import org.example.library.models.User;
import org.example.library.services.AdminService;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

class AdminServiceTest1 {

    @BeforeAll
    static void useTestDB() {
        FileDatabase.useTestDatabase();
    }

    @BeforeEach
    void reset() {
        FileDatabase.reset();
    }

    @Test
    void testValidLogin() {
        JSONObject db = FileDatabase.load();
        JSONArray users = db.getJSONArray("users");

        JSONObject admin = new JSONObject();
        admin.put("username", "admin");
        admin.put("password", "1234");
        admin.put("role", "admin");

        users.put(admin);
        FileDatabase.save(db);

        AdminService service = new AdminService();
        User u = service.login("admin", "1234");

        Assertions.assertNotNull(u);
    }

    @Test
    void testInvalidLogin() {
        AdminService service = new AdminService();
        User u = service.login("wrong", "pass");
        Assertions.assertNull(u);
    }
}
