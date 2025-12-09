package org.example.library.services;

import org.example.library.models.Book;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import org.example.library.models.User;



public class BookService {

    public JSONObject findBookByISBN(String isbn) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("isbn").equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    public void addBook(Book book) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        JSONObject b = new JSONObject();
        b.put("title", book.getTitle());
        b.put("author", book.getAuthor());
        b.put("isbn", book.getIsbn());
        b.put("quantity", book.getQuantity());
        b.put("available", book.isAvailable());

        books.put(b);
        FileDatabase.save(db);
    }

    // ===== Search Functions =====

    public List<Book> searchByISBN(String isbn) {
        List<Book> results = new ArrayList<>();
        JSONObject b = findBookByISBN(isbn);
        if (b != null) {
            results.add(new Book(
                    b.getString("title"),
                    b.getString("author"),
                    b.getString("isbn"),
                    b.getInt("quantity"),
                    b.getBoolean("available")
            ));
        }
        return results;
    }

    public List<Book> searchByTitle(String title) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("title").toLowerCase().contains(title.toLowerCase())) {
                results.add(new Book(
                        b.getString("title"),
                        b.getString("author"),
                        b.getString("isbn"),
                        b.getInt("quantity"),
                        b.getBoolean("available")
                ));
            }
        }
        return results;
    }

    public List<Book> searchByAuthor(String author) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("author").toLowerCase().contains(author.toLowerCase())) {
                results.add(new Book(
                        b.getString("title"),
                        b.getString("author"),
                        b.getString("isbn"),
                        b.getInt("quantity"),
                        b.getBoolean("available")
                ));
            }
        }
        return results;
    }

    public List<Book> getAllBooks() {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            results.add(new Book(
                    b.getString("title"),
                    b.getString("author"),
                    b.getString("isbn"),
                    b.getInt("quantity"),
                    b.getBoolean("available")
            ));
        }
        return results;
    }

    public void checkOverdueBooks() {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        LocalDate today = LocalDate.now();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.optBoolean("borrowed", false)) {
                String due = b.optString("dueDate", null);

                if (due != null && !due.equals("null")) {
                    LocalDate dueDate = LocalDate.parse(due);
                    long daysLate = ChronoUnit.DAYS.between(dueDate, today);

                    if (daysLate > 0) {
                        double fine = daysLate * 10;  // BOOK fine rule
                        b.put("fine", fine);
                    }
                }
            }
        }

        FileDatabase.save(db);
        System.out.println("✔ Book overdue detection complete.");
    }


    // ===================================================
//               BORROW BOOK (Sprint 3)
// ===================================================
    public void borrowBook(String isbn, User currentUser) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.getString("isbn").equals(isbn)) {

                if (!b.getBoolean("available")) {
                    System.out.println("❌ Book not available.");
                    return;
                }

                if (b.getBoolean("borrowed")) {
                    System.out.println("❌ Book already borrowed.");
                    return;
                }

                // reduce quantity
                int qty = b.getInt("quantity");
                b.put("quantity", qty - 1);

                if (qty - 1 == 0)
                    b.put("available", false);

                b.put("borrowed", true);
                b.put("borrowedBy", currentUser.getId());
                b.put("dueDate", LocalDate.now().plusDays(14).toString()); // 14 days
                b.put("fine", 0);

                FileDatabase.save(db);

                System.out.println("📘 Book borrowed! Due date: " + b.getString("dueDate"));
                return;
            }
        }

        System.out.println("❌ Book not found.");
    }


    // ===================================================
//                   PAY FINE
// ===================================================
    public void payFine(String isbn, double amount) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.getString("isbn").equals(isbn)) {

                double fine = b.optDouble("fine", 0);

                if (fine == 0) {
                    System.out.println("✔ No fines for this book.");
                    return;
                }

                if (amount >= fine) {
                    b.put("fine", 0);
                    System.out.println("✔ Fine cleared!");
                } else {
                    b.put("fine", fine - amount);
                    System.out.println("Remaining fine: " + (fine - amount));
                }

                FileDatabase.save(db);
                return;
            }
        }

        System.out.println("❌ Book not found.");
    }


    // ===================================================
//            GET TOTAL FINE FOR A USER
// ===================================================
    public double getTotalBookFineForUser(int userId) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        double total = 0;

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.optInt("borrowedBy", -1) == userId) {
                total += b.optDouble("fine", 0);
            }
        }

        return total;
    }


}
