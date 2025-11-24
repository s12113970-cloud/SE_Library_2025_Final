package org.example.library.menus;

import org.example.library.models.User;
import org.example.library.utils.Input;

public class AdminMenu extends LibrarianMenu {

    public static void show(User currentUser) {

        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1) Add Librarian");
            System.out.println("2) Add Book");           // from LibrarianMenu
            System.out.println("3) Search Book");        // from LibrarianMenu
            System.out.println("4) Show All Books");     // from LibrarianMenu
            System.out.println("5) Logout");

            int choice = Input.number("Choose: ");

            switch (choice) {

                case 1 -> addLibrarian();  // وظيفة خاصة بالأدمِن

                case 2 -> addBook(currentUser);    // موروثة من LibrarianMenu
                case 3 -> searchBook();             // موروث
                case 4 -> showAllBooks();           // موروث

                case 5 -> { return; }

                default -> System.out.println("Invalid option!");
            }
        }
    }

    // =====================================================
    //          ADMIN-ONLY FEATURE: ADD LIBRARIAN
    // =====================================================
    private static void addLibrarian() {
        String username = Input.text("Enter librarian username: ");
        String password = Input.text("Enter password: ");

        System.out.println("✔ Librarian added successfully!");
        System.out.println("(NOTE: You can integrate UserService.add later)");
    }
}
