package org.example.library.menus;

import org.example.library.models.Book;
import org.example.library.models.User;
import org.example.library.services.BookService;
import org.example.library.utils.Input;

import java.util.List;

public class ClientMenu {

    private static BookService bookService = new BookService();

    public static void show(User currentUser) {

        while (true) {
            System.out.println("\n===== Client Menu =====");
            System.out.println("1) Search Book");
            System.out.println("2) Show All Books");
            System.out.println("3) Borrow Book");      // NEW
            System.out.println("4) Pay Fine");         // NEW
            System.out.println("5) Logout");

            int choice = Input.number("Choose: ");

            switch (choice) {

                case 1 -> searchBook();
                case 2 -> showAllBooks();
                case 3 -> borrowBook();     // NEW
                case 4 -> payFine();        // NEW
                case 5 -> {
                    System.out.println("Logged out.");
                    return;
                }

                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ============================
    //       SEARCH BOOK
    // ============================
    private static void searchBook() {
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
    //      BORROW BOOK (NEW)
    // ============================
    private static void borrowBook() {
        String isbn = Input.text("Enter ISBN to borrow: ");
        bookService.borrowBook(isbn);
    }

    // ============================
    //      PAY FINE (NEW)
    // ============================
    private static void payFine() {
        String isbn = Input.text("Enter ISBN: ");
        double amount = Input.number("Enter amount to pay: ");
        bookService.payFine(isbn, amount);
    }

    // ============================
    //      PRINT SEARCH RESULTS
    // ============================
    private static void printSearchResults(List<Book> results) {
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

    // ============================
    //       SHOW ALL BOOKS
    // ============================
    private static void showAllBooks() {
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
}
