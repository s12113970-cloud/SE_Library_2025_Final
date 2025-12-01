package org.example.library.menus;

import org.example.library.models.Book;
import org.example.library.models.CD;
import org.example.library.models.User;
import org.example.library.services.BookService;
import org.example.library.services.CDService;
import org.example.library.utils.Input;

import java.util.List;

public class ClientMenu {

    private static BookService bookService = new BookService();
    private static CDService cdService = new CDService();   // ⭐ NEW

    public static void show(User currentUser) {

        while (true) {
            System.out.println("\n===== Client Menu =====");
            System.out.println("1) Search Book");
            System.out.println("2) Search CD");             // ⭐ NEW
            System.out.println("3) Show All Books");
            System.out.println("4) Show All CDs");          // ⭐ NEW
            System.out.println("5) Borrow Book");
            System.out.println("6) Borrow CD");             // ⭐ NEW
            System.out.println("7) Pay Fine");
            System.out.println("8) Show Total Fine");       // ⭐ NEW
            System.out.println("9) Logout");

            int choice = Input.number("Choose: ");

            switch (choice) {
                case 1 -> searchBook();
                case 2 -> searchCD();                 // ⭐ NEW
                case 3 -> showAllBooks();
                case 4 -> showAllCDs();               // ⭐ NEW
                case 5 -> borrowBook(currentUser);
                case 6 -> borrowCD(currentUser);      // ⭐ NEW
                case 7 -> payFine();
                case 8 -> showTotalFine(currentUser); // ⭐ NEW
                case 9 -> { return; }
                default -> System.out.println("Invalid option!");
            }
        }
    }

    // ============================
    //     SEARCH BOOK
    // ============================
    private static void searchBook() {
        System.out.println("\nSearch Book by:");
        System.out.println("1) ISBN");
        System.out.println("2) Title");
        System.out.println("3) Author");

        int option = Input.number("Choose: ");

        switch (option) {
            case 1 -> {
                String isbn = Input.text("Enter ISBN: ");
                printBookResults(bookService.searchByISBN(isbn));
            }
            case 2 -> {
                String title = Input.text("Enter title: ");
                printBookResults(bookService.searchByTitle(title));
            }
            case 3 -> {
                String author = Input.text("Enter author: ");
                printBookResults(bookService.searchByAuthor(author));
            }
            default -> System.out.println("Invalid choice!");
        }
    }

    // ============================
    //     SEARCH CD  (NEW)
    // ============================
    private static void searchCD() {
        String id = Input.text("Enter CD ID: ");
        var cds = cdService.getAllCDs()
                .stream()
                .filter(cd -> cd.getId().equals(id))
                .toList();

        printCDResults(cds);
    }

    // ============================
    //     BORROW BOOK
    // ============================
    private static void borrowBook(User currentUser) {
        String isbn = Input.text("Enter ISBN to borrow: ");
        bookService.borrowBook(isbn, currentUser);
    }

    // ============================
    //     BORROW CD (NEW)
    // ============================
    private static void borrowCD(User currentUser) {
        String id = Input.text("Enter CD ID to borrow: ");
        cdService.borrowCD(id, currentUser);
    }

    // ============================
    //     PAY FINE (Books only)
    // ============================
    private static void payFine() {
        String isbn = Input.text("Enter ISBN: ");
        double amount = Input.number("Enter amount to pay: ");
        bookService.payFine(isbn, amount);
    }

    // ============================
    //     SHOW TOTAL FINE (Books + CDs)
    // ============================
    private static void showTotalFine(User currentUser) {
        double bookFines = bookService.getTotalBookFineForUser(currentUser.getId());
        double cdFines = cdService.getTotalCdFineForUser(currentUser.getId());

        double total = bookFines + cdFines;

        System.out.println("\n===== Total Media Fines =====");
        System.out.println("Books Fine: " + bookFines + " NIS");
        System.out.println("CDs Fine: " + cdFines + " NIS");
        System.out.println("----------------------------");
        System.out.println("Total Fine: " + total + " NIS");
    }

    // ============================
    //     SHOW ALL BOOKS
    // ============================
    private static void showAllBooks() {
        printBookResults(bookService.getAllBooks());
    }

    // ============================
    //     SHOW ALL CDs (NEW)
    // ============================
    private static void showAllCDs() {
        printCDResults(cdService.getAllCDs());
    }

    // ============================
    //     PRINT HELPERS
    // ============================
    private static void printBookResults(List<Book> results) {
        System.out.println("\n===== Books =====");
        if (results.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        for (Book b : results) {
            System.out.println("- " + b.getTitle() + " | " + b.getAuthor() +
                    " | ISBN: " + b.getIsbn() + " | Qty: " + b.getQuantity());
        }
    }

    private static void printCDResults(List<CD> cds) {
        System.out.println("\n===== CDs =====");
        if (cds.isEmpty()) {
            System.out.println("No CDs found.");
            return;
        }
        for (CD c : cds) {
            System.out.println("- " + c.getId() + " | " + c.getTitle() +
                    " | " + c.getArtist() + " | Qty: " + c.getQuantity());
        }
    }
}
