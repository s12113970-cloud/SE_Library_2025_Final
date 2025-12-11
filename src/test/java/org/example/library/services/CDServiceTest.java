package org.example.library.services;

import org.example.library.models.CD;
import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CDServiceTest {

    private CDService service;

    @BeforeEach
    void setup() {
        service = new CDService();
    }

    // ============================
    // addCD()
    // ============================

    @Test
    void addCD_success() {
        JSONObject db = new JSONObject();
        db.put("cds", new JSONArray());

        CD cd = new CD("C1", "Music", "Artist", 3, true);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(i -> null);

            service.addCD(cd);

            JSONArray arr = db.getJSONArray("cds");

            assertEquals(1, arr.length());
            assertEquals("C1", arr.getJSONObject(0).getString("id"));
            assertEquals(false, arr.getJSONObject(0).getBoolean("borrowed"));
            assertEquals(0, arr.getJSONObject(0).getInt("fine"));
        }
    }

    // ============================
    // getAllCDs()
    // ============================

    @Test
    void getAllCDs_returnsData() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "C1");
        c.put("title", "Hello");
        c.put("artist", "A");
        c.put("quantity", 2);
        c.put("available", true);
        arr.put(c);

        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            List<CD> result = service.getAllCDs();

            assertEquals(1, result.size());
            assertEquals("Hello", result.get(0).getTitle());
        }
    }

    // ============================
    // borrowCD()
    // ============================

    @Test
    void borrowCD_success() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "C1");
        c.put("title", "M");
        c.put("artist", "X");
        c.put("quantity", 1);
        c.put("available", true);
        c.put("borrowed", false);

        arr.put(c);
        db.put("cds", arr);

        User u = new User(7, "d", "p", "user", "x@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(i -> null);

            service.borrowCD("C1", u);

            assertEquals(0, c.getInt("quantity"));
            assertFalse(c.getBoolean("available"));
            assertTrue(c.getBoolean("borrowed"));
            assertEquals(7, c.getInt("borrowedBy"));
        }
    }

    @Test
    void borrowCD_notAvailable() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "C1");
        c.put("available", false);
        c.put("borrowed", false);

        arr.put(c);
        db.put("cds", arr);

        User u = new User(1, "x", "p", "user", "a@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowCD("C1", u);

            assertFalse(c.getBoolean("available"));
        }
    }

    @Test
    void borrowCD_alreadyBorrowed() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "C1");
        c.put("available", true);
        c.put("borrowed", true);

        arr.put(c);
        db.put("cds", arr);

        User u = new User(1, "y", "p", "user", "m@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowCD("C1", u);

            assertTrue(c.getBoolean("borrowed"));
        }
    }

    @Test
    void borrowCD_notFound() {
        JSONObject db = new JSONObject();
        db.put("cds", new JSONArray());

        User u = new User(1, "z", "p", "user", "t@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowCD("NOPE", u);

            assertEquals(0, db.getJSONArray("cds").length());
        }
    }

    // ============================
    // checkOverdueCDs()
    // ============================

    @Test
    void checkOverdueCDs_overdueAddsFine() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "C1");
        c.put("borrowed", true);
        c.put("dueDate", LocalDate.now().minusDays(3).toString());

        arr.put(c);
        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(i -> null);

            service.checkOverdueCDs();

            assertTrue(c.getDouble("fine") > 0);
        }
    }

    @Test
    void checkOverdueCDs_notBorrowed() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("borrowed", false);

        arr.put(c);
        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            service.checkOverdueCDs();

            assertFalse(c.has("fine"));
        }
    }

    // ============================
    // getTotalCdFineForUser()
    // ============================

    @Test
    void getTotalCdFineForUser_works() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c1 = new JSONObject();
        c1.put("borrowedBy", 5);
        c1.put("fine", 4.0);

        JSONObject c2 = new JSONObject();
        c2.put("borrowedBy", 5);
        c2.put("fine", 6.0);

        arr.put(c1);
        arr.put(c2);

        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {

            mock.when(FileDatabase::load).thenReturn(db);

            double total = service.getTotalCdFineForUser(5);

            assertEquals(10.0, total);
        }
    }

    @Test
    void jsonToCD_allFields() throws Exception {
        JSONObject c = new JSONObject();
        c.put("id", "C9");
        c.put("title", "T");
        c.put("artist", "A");
        c.put("quantity", 4);
        c.put("available", true);
        c.put("borrowed", true);
        c.put("dueDate", LocalDate.now().toString());
        c.put("fine", 5.5);
        c.put("borrowedBy", 20);

        // Use reflection to call private method
        var m = CDService.class.getDeclaredMethod("jsonToCD", JSONObject.class);
        m.setAccessible(true);

        CD cd = (CD) m.invoke(service, c);

        assertEquals("C9", cd.getId());
        assertEquals(true, cd.isBorrowed());
        assertEquals(5.5, cd.getFine());
        assertEquals(20, cd.getBorrowedBy());
    }

    @Test
    void jsonToCD_missingOptionalFields() throws Exception {
        JSONObject c = new JSONObject();
        c.put("id", "C10");
        c.put("title", "No Opt");
        c.put("artist", "X");
        c.put("quantity", 2);
        c.put("available", true);

        var m = CDService.class.getDeclaredMethod("jsonToCD", JSONObject.class);
        m.setAccessible(true);

        CD cd = (CD) m.invoke(service, c);

        assertFalse(cd.isBorrowed());
        assertEquals(0.0, cd.getFine());
        assertEquals(-1, cd.getBorrowedBy());
    }


    @Test
    void borrowCD_nullUser() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "E1");
        c.put("title", "Edge");
        c.put("artist", "A");
        c.put("quantity", 1);
        c.put("available", true);
        c.put("borrowed", false);

        arr.put(c);
        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            assertThrows(Exception.class, () -> service.borrowCD("E1", null));
        }
    }

    @Test
    void borrowCD_negativeQuantity() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("id", "N1");
        c.put("quantity", -1);
        c.put("available", true);
        c.put("borrowed", false);

        arr.put(c);
        db.put("cds", arr);

        User u = new User(2, "x", "p", "user", "x@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowCD("N1", u);

            assertTrue(c.getInt("quantity") < 0);
        }
    }

    @Test
    void checkOverdueCDs_dueDateStringNull() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("borrowed", true);
        c.put("dueDate", "null");
        arr.put(c);

        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.checkOverdueCDs();

            assertFalse(c.has("fine"));
        }
    }


    @Test
    void checkOverdueCDs_dueDateMissing() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("borrowed", true);
        // no dueDate
        arr.put(c);

        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.checkOverdueCDs();

            assertFalse(c.has("fine"));
        }
    }


    @Test
    void checkOverdueCDs_notLate() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("borrowed", true);
        c.put("dueDate", LocalDate.now().toString());

        arr.put(c);
        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.checkOverdueCDs();

            assertFalse(c.has("fine"));
        }
    }

    @Test
    void getTotalCdFine_missingFineKey() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject c = new JSONObject();
        c.put("borrowedBy", 7);
        // fine missing
        arr.put(c);

        db.put("cds", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            double total = service.getTotalCdFineForUser(7);

            assertEquals(0.0, total);
        }
    }


}
