package org.example.library.menus;

import org.example.library.models.Book;
import org.example.library.models.User;
import org.example.library.services.BookService;
import org.example.library.storage.FileDatabase;
import org.example.library.utils.Input;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class LibrarianMenu {

    private static BookService bookService = new BookService();

    public static void show(User currentUser) {

        while (true) {
            System.out.println("\n===== Librarian Menu =====");
            System.out.println("1) Add Book");
            System.out.println("2) Search Book");
            System.out.println("3) Show All Books");
            System.out.println("4) Logout");

            int choice = Input.number("Choose: ");

            switch (choice) {
                case 1 -> addBook(currentUser);
                case 2 -> searchBook();
                case 3 -> showAllBooks();
                case 4 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ============================
    //       ADD BOOK (MOVING FROM MAIN)
    // ============================
    protected static void addBook(User currentUser) {

        if (!currentUser.isAdmin() && !currentUser.getRole().equalsIgnoreCase("librarian")) {
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
                // Load complete DB
                JSONObject db = FileDatabase.load();
                JSONArray books = db.getJSONArray("books");

                JSONObject target = null;
                for (int i = 0; i < books.length(); i++) {
                    JSONObject obj = books.getJSONObject(i);
                    if (obj.getString("isbn").equals(isbn)) {
                        target = obj;
                        break;
                    }
                }

                if (target != null) {
                    int addQty = Input.number("Enter quantity to add: ");
                    int newQty = target.getInt("quantity") + addQty;

                    target.put("quantity", newQty);
                    FileDatabase.save(db);

                    System.out.println("✔ Quantity updated to: " + newQty);
                }
                return;
            }
            else if (option == 2) {

                String newIsbn;
                while (true) {
                    newIsbn = Input.text("Enter new ISBN: ");

                    if (bookService.findBookByISBN(newIsbn) == null) {
                        break;
                    }
                    System.out.println("❌ ISBN already exists! Please enter a different one.");
                }

                String title = Input.text("Enter title: ");
                String author = Input.text("Enter author: ");
                int qty = Input.number("Enter quantity: ");

                Book newBook = new Book(title, author, newIsbn, qty);
                bookService.addBook(newBook);

                System.out.println("✔ New edition added successfully!");
                return;
            }
            else {
                System.out.println("Cancelled.");
                return;
            }
        }

        // Add new book
        String title = Input.text("Enter title: ");
        String author = Input.text("Enter author: ");
        int qty = Input.number("Enter quantity: ");

        Book b = new Book(title, author, isbn, qty);
        bookService.addBook(b);

        System.out.println("✔ Book added successfully!");
    }


    // ============================
    //         SEARCH BOOK
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
    //      PRINT SEARCH RESULTS
    // ============================
    protected static void printSearchResults(List<Book> results) {
        System.out.println("\n===== Search Results =====");
        if (results.isEmpty()) {
            System.out.println("No books found.");
            return;
        }

        for (Book b : results) {
            System.out.println("- " + b.getTitle() + " | " + b.getAuthor() +
                    " | ISBN:" + b.getIsbn() + " | Qty: " + b.getQuantity());
        }
    }


    // ============================
    //        SHOW ALL BOOKS
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
                    " | ISBN:" + b.getIsbn() + " | Qty:" + b.getQuantity());
        }
    }
}
