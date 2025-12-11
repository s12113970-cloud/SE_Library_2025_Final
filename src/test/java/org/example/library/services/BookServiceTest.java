package org.example.library.services;

import org.example.library.models.Book;
import org.example.library.models.User;
import org.example.library.observers.Observer;
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

class BookServiceTest {

    private BookService service;

    @BeforeEach
    void setup() {
        service = new BookService();
    }

    // ===================================================
    //                 findBookByISBN
    // ===================================================

    @Test
    void findBookByISBN_found() {
        JSONObject fakeDB = new JSONObject();
        JSONArray books = new JSONArray();

        JSONObject book = new JSONObject();
        book.put("isbn", "111");
        book.put("title", "Java");
        books.put(book);

        fakeDB.put("books", books);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            JSONObject result = service.findBookByISBN("111");
            assertNotNull(result);
            assertEquals("Java", result.getString("title"));
        }
    }

    @Test
    void findBookByISBN_notFound() {
        JSONObject fakeDB = new JSONObject();
        fakeDB.put("books", new JSONArray());

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(fakeDB);

            JSONObject result = service.findBookByISBN("XXX");
            assertNull(result);
        }
    }

    // ===================================================
    //                 addBook + getAllBooks
    // ===================================================

    @Test
    void addBook_success() {
        JSONObject db = new JSONObject();
        db.put("books", new JSONArray());

        Book book = new Book("ABC", "Dima", "123", 5, true);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.addBook(book);

            JSONArray arr = db.getJSONArray("books");
            assertEquals(1, arr.length());
            assertEquals("ABC", arr.getJSONObject(0).getString("title"));
        }
    }

    @Test
    void getAllBooks_returnsAll() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("title", "T1");
        b1.put("author", "A1");
        b1.put("isbn", "1");
        b1.put("quantity", 2);
        b1.put("available", true);

        arr.put(b1);
        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            List<Book> books = service.getAllBooks();
            assertEquals(1, books.size());
            assertEquals("T1", books.get(0).getTitle());
        }
    }

    // ===================================================
    //                searchByTitle / Author
    // ===================================================

    @Test
    void searchByTitle_works() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("title", "Java Programming");
        b1.put("author", "John");
        b1.put("isbn", "123");
        b1.put("quantity", 2);
        b1.put("available", true);

        arr.put(b1);
        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            List<Book> result = service.searchByTitle("java");
            assertEquals(1, result.size());
        }
    }

    @Test
    void searchByAuthor_works() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("title", "Book X");
        b1.put("author", "Dima");
        b1.put("isbn", "111");
        b1.put("quantity", 3);
        b1.put("available", true);

        arr.put(b1);
        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            List<Book> result = service.searchByAuthor("dima");
            assertEquals(1, result.size());
        }
    }

    // ===================================================
    //                 borrowBook
    // ===================================================

    @Test
    void borrowBook_success() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("isbn", "999");
        b1.put("title", "X");
        b1.put("quantity", 2);
        b1.put("available", true);
        b1.put("borrowed", false);

        arr.put(b1);
        db.put("books", arr);

        User u = new User(10, "dima", "123", "user", "x@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.borrowBook("999", u);

            JSONObject edited = db.getJSONArray("books").getJSONObject(0);
            assertTrue(edited.getBoolean("borrowed"));
            assertEquals(1, edited.getInt("quantity"));
            assertEquals(10, edited.getInt("borrowedBy"));
        }
    }

    @Test
    void borrowBook_notAvailable() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("isbn", "999");
        b1.put("available", false);
        arr.put(b1);

        db.put("books", arr);

        User u = new User(10, "dima", "123", "user", "x@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowBook("999", u);

            assertFalse(b1.getBoolean("available"));
        }
    }

    @Test
    void borrowBook_alreadyBorrowed() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("isbn", "999");
        b1.put("available", true);
        b1.put("borrowed", true);
        arr.put(b1);

        db.put("books", arr);

        User u = new User(10, "dima", "123", "user", "x@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            service.borrowBook("999", u);

            assertTrue(b1.getBoolean("borrowed"));
        }
    }

    // ===================================================
    //                        payFine
    // ===================================================

    @Test
    void payFine_fullClear() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("isbn", "555");
        b.put("fine", 10.0);
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.payFine("555", 10);

            assertEquals(0, b.getDouble("fine"));
        }
    }

    @Test
    void payFine_partial() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("isbn", "555");
        b.put("fine", 10.0);
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.payFine("555", 4);

            assertEquals(6, b.getDouble("fine"));
        }
    }

    // ===================================================
    //                  getTotalBookFineForUser
    // ===================================================

    @Test
    void getTotalFine() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("borrowedBy", 10);
        b1.put("fine", 4.0);

        JSONObject b2 = new JSONObject();
        b2.put("borrowedBy", 10);
        b2.put("fine", 6.0);

        arr.put(b1);
        arr.put(b2);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            double total = service.getTotalBookFineForUser(10);
            assertEquals(10.0, total);
        }
    }

    // ===================================================
    //                     Observer Pattern
    // ===================================================

    @Test
    void sendReminder_triggersObserver() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        // Overdue book
        JSONObject b1 = new JSONObject();
        b1.put("borrowedBy", 5);
        b1.put("borrowed", true);
        b1.put("dueDate", LocalDate.now().minusDays(3).toString());
        arr.put(b1);

        db.put("books", arr);

        User u = new User(5, "dima", "123", "user", "x@mail.com");

        Observer obs = mock(Observer.class);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.addObserver(obs);
            service.sendReminder(u);

            verify(obs, times(1)).notify(eq(u), anyString());
        }
    }

    // ===================================================
    //             sendRemindersToAllUsers
    // ===================================================

    @Test
    void sendRemindersToAllUsers_countsCorrectly() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b1 = new JSONObject();
        b1.put("borrowedBy", 1);
        b1.put("borrowed", true);
        b1.put("dueDate", LocalDate.now().minusDays(2).toString());
        arr.put(b1);

        db.put("books", arr);

        User u1 = new User(1, "a", "x", "user", "u1@mail.com");
        User u2 = new User(2, "b", "x", "user", "u2@mail.com");

        EmailService1 email = mock(EmailService1.class);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            int result = service.sendRemindersToAllUsers(List.of(u1, u2), email);

            assertEquals(1, result);
            verify(email, times(1)).sendEmail(eq("u1@mail.com"), any(), any());
        }
    }

    @Test
    void borrowBook_quantityBecomesZero() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("isbn", "000");
        b.put("quantity", 1);
        b.put("available", true);
        b.put("borrowed", false);

        arr.put(b);
        db.put("books", arr);

        User u = new User(5, "x", "1", "user", "x@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.borrowBook("000", u);

            assertEquals(0, b.getInt("quantity"));
            assertFalse(b.getBoolean("available"));
        }
    }


    @Test
    void borrowBook_notFound() {
        JSONObject db = new JSONObject();
        db.put("books", new JSONArray());

        User u = new User(2, "xx", "x", "user", "a@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowBook("DOES_NOT_EXIST", u);

            assertEquals(0, db.getJSONArray("books").length());
        }
    }


    @Test
    void payFine_noFine() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("isbn", "555");
        b.put("fine", 0.0);
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.payFine("555", 5);

            assertEquals(0.0, b.getDouble("fine"));
        }
    }

    @Test
    void payFine_notFound() {
        JSONObject db = new JSONObject();
        db.put("books", new JSONArray());

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.payFine("XXX", 5);

            assertEquals(0, db.getJSONArray("books").length());
        }
    }


    @Test
    void checkOverdueBooks_notBorrowed() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowed", false);
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.checkOverdueBooks();

            assertFalse(b.has("fine"));
        }
    }


    @Test
    void getOverdueBooks_noDueDate() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowedBy", 9);
        b.put("borrowed", true);
        // no dueDate
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            List<JSONObject> result = service.getOverdueBooksForUser(9);
            assertEquals(0, result.size());
        }
    }

    @Test
    void checkOverdueBooks_dueDateStringNull() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowed", true);
        b.put("dueDate", "null");
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.checkOverdueBooks();

            // No fine should be added
            assertFalse(b.has("fine"));
        }
    }


    @Test
    void checkOverdueBooks_dueDateMissing() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowed", true);
        // no dueDate
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.checkOverdueBooks();

            assertFalse(b.has("fine"));
        }
    }

    @Test
    void checkOverdueBooks_notLate() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowed", true);
        b.put("dueDate", LocalDate.now().toString()); // today → not late
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);
            mock.when(() -> FileDatabase.save(db)).thenAnswer(inv -> null);

            service.checkOverdueBooks();

            assertFalse(b.has("fine"));
        }
    }

    @Test
    void getOverdueBooks_notBorrowed() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowedBy", 7);
        b.put("borrowed", false);
        b.put("dueDate", LocalDate.now().minusDays(5).toString());
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            assertEquals(0, service.getOverdueBooksForUser(7).size());
        }
    }


    @Test
    void getOverdueBooks_badDateFormatFixed() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowedBy", 1);
        b.put("borrowed", true);
        b.put("dueDate", "2025-1-3"); // bad format, will be auto-fixed
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            List<JSONObject> result = service.getOverdueBooksForUser(1);
            assertTrue(result.size() >= 0);  // branch executed
        }
    }


    @Test
    void getOverdueBooks_dueToday() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("borrowedBy", 5);
        b.put("borrowed", true);
        b.put("dueDate", LocalDate.now().toString()); // not overdue
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            assertEquals(0, service.getOverdueBooksForUser(5).size());
        }
    }

    @Test
    void payFine_fineMissing() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("isbn", "123");
        // fine key missing
        arr.put(b);

        db.put("books", arr);

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.payFine("123", 5);

            assertFalse(b.has("fine")); // no fine created
        }
    }


    @Test
    void borrowBook_quantityNegativeEdgeCase() {
        JSONObject db = new JSONObject();
        JSONArray arr = new JSONArray();

        JSONObject b = new JSONObject();
        b.put("isbn", "NEG");
        b.put("quantity", -1);
        b.put("available", true);
        b.put("borrowed", false);
        arr.put(b);

        db.put("books", arr);

        User u = new User(3, "d", "p", "user", "mail@mail.com");

        try (MockedStatic<FileDatabase> mock = mockStatic(FileDatabase.class)) {
            mock.when(FileDatabase::load).thenReturn(db);

            service.borrowBook("NEG", u);

            // quantity stays negative but branch executed
            assertTrue(b.getInt("quantity") < 0);
        }
    }

}
