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
import org.example.library.strategies.FineStrategy;
import org.example.library.strategies.BookFineStrategy;
import org.example.library.observers.Observer;

/**
 * Service class responsible for handling all operations related to books:
 * searching, borrowing, fines, overdue detection, reminders, and observer notifications.
 *
 * <p>This class communicates directly with the {@link FileDatabase} to read
 * and update book information.</p>
 */

public class BookService {


    /**
     * Finds a book in the database by its ISBN.
     *
     * @param isbn the ISBN code of the desired book
     * @return a {@link JSONObject} containing the book data or {@code null} if not found
     */

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

    // ================= OBSERVER PATTERN ==================
    /** List of observers that should be notified about overdue reminders. */

    private List<Observer> observers = new ArrayList<>();

    /**
     * Registers an observer that will receive notifications.
     *
     * @param observer the observer to register
     */

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Removes an existing observer from the notification list.
     *
     * @param observer the observer to remove
     */

    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }


    /**
     * Notifies all observers about a reminder message.
     *
     * @param user    the user the message concerns
     * @param message the notification content
     */

    private void notifyObservers(User user, String message) {
        for (Observer obs : observers) {
            obs.notify(user, message);
        }
    }


    /**
     * Adds a new book to the database.
     *
     * @param book the {@link Book} object to register
     */

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

    /**
     * Searches for a book by ISBN and returns a list of matching results.
     *
     * @param isbn the ISBN value to search for
     * @return list of matching {@link Book} objects
     */

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


    /**
     * Searches for books that contain the specified title.
     *
     * @param title the title substring to search for
     * @return list of matching books
     */

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


    /**
     * Searches for books written by a specific author.
     *
     * @param author the author name or substring
     * @return list of matching books
     */

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


    /**
     * Returns all books stored in the database.
     *
     * @return list of all books
     */

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


    /**
     * Checks all borrowed books and applies fines using the {@link FineStrategy}.
     * If a book is overdue, a fine is calculated based on days late.
     */

    public void checkOverdueBooks() {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        LocalDate today = LocalDate.now();

        // Create strategy once (for books only)
        FineStrategy strategy = new BookFineStrategy();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.optBoolean("borrowed", false)) {
                String due = b.optString("dueDate", null);

                if (due != null && !due.equals("null")) {
                    LocalDate dueDate = LocalDate.parse(due);
                    long daysLate = ChronoUnit.DAYS.between(dueDate, today);

                    if (daysLate > 0) {

                        // ⭐ Use STRATEGY to calculate fine
                        double fine = strategy.calculateFine((int) daysLate);
                        b.put("fine", fine);
                    }
                }
            }
        }

        FileDatabase.save(db);
        System.out.println("✔ Book overdue detection complete (Strategy Pattern applied).");
    }



    /**
     * Allows a user to borrow a book if available.
     *
     * @param isbn        the ISBN of the book to borrow
     * @param currentUser the user borrowing the book
     */

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


    /**
     * Allows a user to pay part or all of their book fine.
     *
     * @param isbn   the ISBN of the book
     * @param amount the payment amount
     */

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


    /**
     * Calculates the total accumulated fine for all books borrowed by a specific user.
     *
     * @param userId the user's ID
     * @return total fine amount
     */


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


    /**
     * Retrieves all overdue books for a specific user.
     *
     * @param userId the user's ID
     * @return list of overdue books as {@link JSONObject}
     */

    public List<JSONObject> getOverdueBooksForUser(int userId) {

        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        List<JSONObject> overdue = new ArrayList<>();
        LocalDate today = LocalDate.now();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);

            if (b.optInt("borrowedBy", -1) == userId &&
                    b.optBoolean("borrowed", false) &&
                    b.has("dueDate")) {


                String raw = b.getString("dueDate");

                // إصلاح صيغة التاريخ تلقائيًا
                if (raw.matches("\\d{4}-\\d-\\d{2}")) raw = raw.replaceFirst("-(\\d)-", "-0$1-");
                if (raw.matches("\\d{4}-\\d{2}-\\d")) raw = raw.replaceFirst("-(\\d)$", "-0$1");
                if (raw.matches("\\d{4}-\\d-\\d"))    raw = raw.replaceFirst("-(\\d)-", "-0$1-").replaceFirst("-(\\d)$", "-0$1");

                LocalDate due = LocalDate.parse(raw);


                if (due.isBefore(today)) {
                    overdue.add(b);
                }
            }
        }

        return overdue;
    }


    /**
     * Sends an overdue reminder to a specific user.
     * If the user has no overdue books, no notification is triggered.
     *
     * @param user the user to notify
     */

    public void sendReminder(User user) {

        List<JSONObject> overdue = getOverdueBooksForUser(user.getId());
        int n = overdue.size();

        if (n == 0) {
            System.out.println("✔ No overdue books for: " + user.getUsername());
            return;
        }

        String msg = "You have " + n + " overdue book(s).";

        // 🔥 الآن BookService لا يرسل إيميل مباشرة
        // ❗ فقط ينادي notifyObservers
        notifyObservers(user, msg);

        System.out.println("📧 Reminder notification triggered for " + user.getUsername());
    }

    /**
     * Sends reminders (real emails) to all users who have overdue books.
     *
     * @param users        list of users to check
     * @param emailService service used to send the email
     * @return total number of reminders sent
     */


    public int sendRemindersToAllUsers(List<User> users, EmailService1 emailService) {

        int totalSent = 0;

        for (User user : users) {

            List<JSONObject> overdue = getOverdueBooksForUser(user.getId());

            if (!overdue.isEmpty()) {

                String message = "You have " + overdue.size() + " overdue book(s).";

                // ⭐ send real email
                emailService.sendEmail(
                        user.getEmail(),         // <– الإيميل الحقيقي للمستخدم
                        "Overdue Reminder",
                        message
                );

                totalSent++;
            }
        }

        return totalSent;
    }




}
