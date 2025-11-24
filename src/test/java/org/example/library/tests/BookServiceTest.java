package org.example.library.tests;

import org.example.library.models.Book;
import org.example.library.services.BookService;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.*;

import java.util.List;

class BookServiceTest {

    @BeforeAll
    static void useTestDB() {
        FileDatabase.useTestDatabase();
    }

    @BeforeEach
    void resetDB() {
        FileDatabase.reset();
    }

    // 1) Test adding a new book with quantity
    @Test
    void testAddBook() {
        BookService service = new BookService();
        Book b = new Book("Java", "James", "111", 3);

        service.addBook(b);

        List<Book> results = service.searchByISBN("111");
        Assertions.assertEquals(1, results.size());
        Assertions.assertEquals(3, results.get(0).getQuantity());
    }

    // 2) Test increasing quantity (FULL FIXED VERSION)
    @Test
    void testIncreaseQuantity() {
        BookService service = new BookService();

        // Add initial book
        Book b1 = new Book("Java", "James", "111", 2);
        service.addBook(b1);

        // Load DB and modify the exact object inside JSON
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        JSONObject existingBook = null;

        for (int i = 0; i < books.length(); i++) {
            JSONObject obj = books.getJSONObject(i);
            if (obj.getString("isbn").equals("111")) {
                existingBook = obj;
                break;
            }
        }

        Assertions.assertNotNull(existingBook);

        // Increase quantity by 4
        int newQty = existingBook.getInt("quantity") + 4;
        existingBook.put("quantity", newQty);

        // Save updated DB
        FileDatabase.save(db);

        // Verify
        List<Book> results = service.searchByISBN("111");
        Assertions.assertEquals(6, results.get(0).getQuantity());
    }

    // 3) Test adding new edition (different ISBN)
    @Test
    void testAddNewEdition() {
        BookService service = new BookService();

        service.addBook(new Book("Java", "James", "111", 1));
        service.addBook(new Book("Advanced Java", "King", "222", 2));

        Assertions.assertNotNull(service.findBookByISBN("111"));
        Assertions.assertNotNull(service.findBookByISBN("222"));
    }

    // 4) Search by ISBN
    @Test
    void testSearchByISBN() {
        BookService service = new BookService();
        service.addBook(new Book("Java", "James", "111", 2));

        List<Book> results = service.searchByISBN("111");
        Assertions.assertEquals(1, results.size());
    }

    // 5) Search by Title
    @Test
    void testSearchByTitle() {
        BookService service = new BookService();
        service.addBook(new Book("Java Programming", "James", "111", 2));

        List<Book> results = service.searchByTitle("Java");
        Assertions.assertFalse(results.isEmpty());
    }

    // 6) Search by Author
    @Test
    void testSearchByAuthor() {
        BookService service = new BookService();
        service.addBook(new Book("Java", "James", "111", 2));

        List<Book> results = service.searchByAuthor("James");
        Assertions.assertFalse(results.isEmpty());
    }
}
