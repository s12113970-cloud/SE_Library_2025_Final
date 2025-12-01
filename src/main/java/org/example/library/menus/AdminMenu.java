package org.example.library.menus;

import org.example.library.models.User;
import org.example.library.services.AdminService;
import org.example.library.utils.Input;

public class AdminMenu extends LibrarianMenu {

    private static AdminService adminService = new AdminService();

    public static void show(User currentUser) {

        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1) Add Librarian");
            System.out.println("2) Add Book");
            System.out.println("3) Search Book");
            System.out.println("4) Show All Books");
            System.out.println("5) Unregister User");      // ⭐ تمت إضافتها
            System.out.println("6) Logout");

            int choice = Input.number("Choose: ");

            switch (choice) {

                case 1 -> addLibrarian();

                case 2 -> addBook(currentUser);
                case 3 -> searchBook();
                case 4 -> showAllBooks();

                case 5 -> unregisterUser(currentUser);   // ⭐ الجديد

                case 6 -> { return; }

                default -> System.out.println("Invalid option!");
            }
        }
    }

    // =====================================================
    //                ADD LIBRARIAN (admin only)
    // =====================================================
    private static void addLibrarian() {
        String username = Input.text("Enter librarian username: ");
        String password = Input.text("Enter password: ");

        System.out.println("✔ Librarian added successfully!");
        System.out.println("(NOTE: Implement storing in JSON later)");
    }

    // =====================================================
    //             UNREGISTER USER (SPRINT 4)
    // =====================================================
    private static void unregisterUser(User currentUser) {

        String username = Input.text("Enter username to unregister: ");

        boolean result = adminService.unregisterUser(username, currentUser);

        if (!result) {
            System.out.println("❌ Failed to unregister user.");
        }
    }
}
