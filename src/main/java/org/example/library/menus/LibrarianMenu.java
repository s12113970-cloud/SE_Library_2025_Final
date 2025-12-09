package org.example.library.menus;

import org.example.library.models.Book;
import org.example.library.models.CD;
import org.example.library.models.User;
import org.example.library.services.BookService;
import org.example.library.services.CDService;
import org.example.library.utils.Input;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class LibrarianMenu {

    private static BookService bookService = new BookService();
    private static CDService cdService = new CDService();  // ⭐ NEW SERVICE

    public static void show(User currentUser) {

        while (true) {
            System.out.println("\n===== Librarian Menu =====");
            System.out.println("1) Add Book");
            System.out.println("2) Add CD");
            System.out.println("3) Search Book");
            System.out.println("4) Show All Books");
            System.out.println("5) Show All CDs");
            System.out.println("6) Check Overdue Media (Books + CDs)");
            System.out.println("7) Logout");

            int choice = Input.number("Choose: ");

            switch (choice) {
                case 1 -> addBook(currentUser);
                case 2 -> addCD(currentUser);
                case 3 -> searchBook();
                case 4 -> showAllBooks();
                case 5 -> showAllCDs();
                case 6 -> checkOverdueMedia();
                case 7 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ============================
    //       ADD BOOK
    // ============================
    protected static void addBook(User currentUser) {

        if (!currentUser.isAdmin() &&
                !currentUser.getRole().equalsIgnoreCase("librarian")) {
            System.out.println("❌ Only admin or librarian can add books!");
            return;
        }

        String isbn = Input.text("Enter ISBN: ");
        JSONObject existingBook = bookService.findBookByISBN(isbn);

        if (existingBook != null) {
            System.out.println("\nBook already exists:");
            System.out.println("Title: " + existingBook.getString("title"));
            System.out.println("Author: " + existingBook.getString("author"));
            System.out.println("Quantity: " + existingBook.getInt("quantity"));

            System.out.println("\nOptions:");
            System.out.println("1) Increase quantity");
            System.out.println("2) Add new edition (new ISBN)");
            System.out.println("3) Cancel");

            int option = Input.number("Choose: ");

            if (option == 1) {
                JSONObject db = FileDatabase.load();
                JSONArray books = db.getJSONArray("books");

                for (int i = 0; i < books.length(); i++) {
                    JSONObject obj = books.getJSONObject(i);

                    if (obj.getString("isbn").equals(isbn)) {
                        int addQty = Input.number("Enter quantity to add: ");
                        int newQty = obj.getInt("quantity") + addQty;

                        obj.put("quantity", newQty);
                        FileDatabase.save(db);
                        System.out.println("✔ Quantity updated to: " + newQty);
                        return;
                    }
                }
            }

            else if (option == 2) {
                String newIsbn;
                while (true) {
                    newIsbn = Input.text("Enter new ISBN: ");
                    if (bookService.findBookByISBN(newIsbn) == null) break;

                    System.out.println("❌ ISBN already exists! Try another.");
                }

                String title = Input.text("Enter title: ");
                String author = Input.text("Enter author: ");
                int qty = Input.number("Enter quantity: ");

                Book newBook = new Book(title, author, newIsbn, qty, qty > 0);
                bookService.addBook(newBook);

                System.out.println("✔ New edition added!");
            }

            return;
        }

        // brand new book
        String title = Input.text("Enter title: ");
        String author = Input.text("Enter author: ");
        int qty = Input.number("Enter quantity: ");

        Book newBook = new Book(title, author, isbn, qty, qty > 0);
        bookService.addBook(newBook);

        System.out.println("✔ Book added successfully!");
    }

    // ============================
    //       ADD CD
    // ============================
    private static void addCD(User currentUser) {

        if (!currentUser.isAdmin() &&
                !currentUser.getRole().equalsIgnoreCase("librarian")) {
            System.out.println("❌ Only admin or librarian can add CDs!");
            return;
        }

        String id = Input.text("Enter CD ID: ");
        String title = Input.text("Enter CD Title: ");
        String artist = Input.text("Enter Artist Name: ");
        int qty = Input.number("Enter quantity: ");

        CD cd = new CD(id, title, artist, qty, qty > 0);
        cdService.addCD(cd);

        System.out.println("✔ CD added successfully!");
    }

    // ============================
    //     SEARCH BOOK
    // ============================
    protected static void searchBook() {
        System.out.println("\nSearch by:");
        System.out.println("1) ISBN");
        System.out.println("2) Title");
        System.out.println("3) Author");

        int option = Input.number("Choose: ");

        switch (option) {
            case 1 -> {
                String isbn = Input.text("Enter ISBN: ");
                printSearchResults(bookService.searchByISBN(isbn));
            }
            case 2 -> {
                String title = Input.text("Enter title: ");
                printSearchResults(bookService.searchByTitle(title));
            }
            case 3 -> {
                String author = Input.text("Enter author: ");
                printSearchResults(bookService.searchByAuthor(author));
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    // ============================
    //      SHOW ALL CDs
    // ============================
    private static void showAllCDs() {
        List<CD> cds = cdService.getAllCDs();

        System.out.println("\n===== All CDs =====");
        if (cds.isEmpty()) {
            System.out.println("No CDs available.");
            return;
        }

        for (CD cd : cds) {
            System.out.println("- " + cd.getId() + " | " + cd.getTitle() +
                    " | " + cd.getArtist() + " | Qty: " + cd.getQuantity());
        }
    }

    // ============================
    //      SHOW ALL BOOKS
    // ============================
    protected static void showAllBooks() {
        List<Book> results = bookService.getAllBooks();

        System.out.println("\n===== All Books =====");
        if (results.isEmpty()) {
            System.out.println("No books available.");
            return;
        }

        for (Book b : results) {
            System.out.println("- " + b.getTitle() + " | " + b.getAuthor() +
                    " | ISBN: " + b.getIsbn() + " | Qty: " + b.getQuantity());
        }
    }

    // ============================
    //  CHECK OVERDUE (BOOKS + CDs)
    // ============================
    private static void checkOverdueMedia() {
        System.out.println("\nRunning overdue detection...");

        bookService.checkOverdueBooks();
        cdService.checkOverdueCDs();

        System.out.println("✔ Overdue detection completed for Books + CDs!");
    }

    // ============================
    //   PRINT SEARCH RESULTS
    // ============================
    protected static void printSearchResults(List<Book> results) {
        System.out.println("\n===== Search Results =====");

        if (results.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        for (Book b : results) {
            System.out.println("- " + b.getTitle() + " | " + b.getAuthor() +
                    " | ISBN: " + b.getIsbn() + " | Qty: " + b.getQuantity());
        }
    }
}
