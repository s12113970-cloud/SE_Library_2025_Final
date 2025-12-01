package org.example.library.services;

import org.example.library.models.Book;
import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    // 🔍 Find book by ISBN (returns JSON object)
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

    // ➕ Add new Book
    public void addBook(Book book) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        JSONObject b = new JSONObject();
        b.put("title", book.getTitle());
        b.put("author", book.getAuthor());
        b.put("isbn", book.getIsbn());
        b.put("quantity", book.getQuantity());
        b.put("available", book.isAvailable());

        // Sprint 2 fields
        b.put("borrowed", false);
        b.put("dueDate", JSONObject.NULL);
        b.put("fine", 0.0);
        b.put("borrowedBy", JSONObject.NULL);

        books.put(b);
        FileDatabase.save(db);
    }

    // ============================
    //   🔎 Search Functions
    // ============================

    public List<Book> searchByISBN(String isbn) {
        List<Book> results = new ArrayList<>();
        JSONObject b = findBookByISBN(isbn);
        if (b != null) results.add(jsonToBook(b));
        return results;
    }

    public List<Book> searchByTitle(String title) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("title").toLowerCase().contains(title.toLowerCase())) {
                results.add(jsonToBook(b));
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
                results.add(jsonToBook(b));
            }
        }
        return results;
    }

    public List<Book> getAllBooks() {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            results.add(jsonToBook(books.getJSONObject(i)));
        }
        return results;
    }

    // ============================
    //     📌 Sprint 4 Borrow Rules
    // ============================

    public void borrowBook(String isbn, User currentUser) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        // 1️⃣ Restriction: User cannot borrow if they have unpaid fines
        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.optInt("borrowedBy", -1) == currentUser.getId() &&
                    b.optDouble("fine", 0) > 0) {

                System.out.println("❌ You have unpaid fines. Pay them before borrowing.");
                return;
            }
        }

        // 2️⃣ Restriction: User cannot borrow if they have overdue books
        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.optInt("borrowedBy", -1) == currentUser.getId() &&
                    b.optBoolean("borrowed", false)) {

                String due = b.optString("dueDate", null);
                if (due != null && !due.equals("null")) {
                    LocalDate d = LocalDate.parse(due);
                    if (d.isBefore(LocalDate.now())) {
                        System.out.println("❌ You have overdue books. Return them before borrowing.");
                        return;
                    }
                }
            }
        }

        // 3️⃣ Proceed with borrowing
        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.getString("isbn").equals(isbn)) {

                if (!b.getBoolean("available")) {
                    System.out.println("❌ Book is out of stock.");
                    return;
                }

                if (b.optBoolean("borrowed", false)) {
                    System.out.println("❌ This book is already borrowed.");
                    return;
                }

                // Update stock
                int qty = b.getInt("quantity");
                b.put("quantity", qty - 1);
                if (qty - 1 == 0) b.put("available", false);

                // Borrowing details
                b.put("borrowed", true);
                b.put("dueDate", LocalDate.now().plusDays(28).toString());
                b.put("fine", 0.0);
                b.put("borrowedBy", currentUser.getId());

                FileDatabase.save(db);
                System.out.println("📘 Borrowed! Due date: " + b.getString("dueDate"));
                return;
            }
        }

        System.out.println("❌ Book not found.");
    }

    // ============================
    //     🕒 Overdue Detection
    // ============================

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
                        double fine = daysLate * 0.5; // 50 cents per day
                        b.put("fine", fine);
                    }
                }
            }
        }

        FileDatabase.save(db);
        System.out.println("✔ Overdue detection complete.");
    }

    // ============================
    //       💰 Pay Fine
    // ============================

    public void payFine(String isbn, double amount) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.getString("isbn").equals(isbn)) {

                double fine = b.optDouble("fine", 0);

                if (fine == 0) {
                    System.out.println("✔ No fine to pay.");
                    return;
                }

                double remaining = fine - amount;

                if (remaining <= 0) {
                    b.put("fine", 0.0);
                    System.out.println("✔ Fine fully paid!");
                } else {
                    b.put("fine", remaining);
                    System.out.println("Remaining fine: " + remaining);
                }

                FileDatabase.save(db);
                return;
            }
        }

        System.out.println("❌ Book not found.");
    }

    // ============================
    //   🔄 Helper: JSON → Book
    // ============================

    private Book jsonToBook(JSONObject b) {

        Book book = new Book(
                b.getString("title"),
                b.getString("author"),
                b.getString("isbn"),
                b.getInt("quantity"),
                b.getBoolean("available")
        );

        if (b.has("borrowed"))
            book.setBorrowed(b.getBoolean("borrowed"));

        if (b.has("dueDate") && !b.isNull("dueDate"))
            book.setDueDate(LocalDate.parse(b.getString("dueDate")));

        if (b.has("fine"))
            book.setFine(b.getDouble("fine"));

        return book;
    }

    // ============================
//   Total fine for books for this user
// ============================
    public double getTotalBookFineForUser(int userId) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        double total = 0;

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            int borrowedBy = b.optInt("borrowedBy", -1);

            if (borrowedBy == userId) {
                total += b.optDouble("fine", 0);
            }
        }

        return total;
    }

}
