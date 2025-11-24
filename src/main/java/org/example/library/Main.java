package org.example.library;

import org.example.library.models.User;
import org.example.library.menus.AdminMenu;
import org.example.library.menus.ClientMenu;
import org.example.library.menus.LibrarianMenu;
import org.example.library.services.AdminService;
import org.example.library.storage.FileDatabase;
import org.example.library.utils.Input;

public class Main {

    private static AdminService adminService = new AdminService();
    private static User currentUser = null;

    public static void main(String[] args) {

        // Use main database
        FileDatabase.useMainDatabase();

        while (true) {
            System.out.println("\n===== Library System =====");
            System.out.println("1) Login");
            System.out.println("2) Exit");

            int choice = Input.number("Choose option: ");

            switch (choice) {
                case 1 -> login();
                case 2 -> {
                    System.out.println("Goodbye ❤️");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private static void login() {
        String username = Input.text("Enter username: ");
        String password = Input.text("Enter password: ");

        currentUser = adminService.login(username, password);

        if (currentUser == null) {
            System.out.println("❌ Login failed!");
        } else {
            System.out.println("✔ Login successful (" + currentUser.getUsername() + ")");
            openUserMenu();
        }
    }

    private static void openUserMenu() {
        String role = currentUser.getRole().toLowerCase();

        switch (role) {

            case "admin" -> {
                System.out.println("Opening Admin Menu...");
                AdminMenu.show(currentUser);
            }

            case "librarian" -> {
                System.out.println("Opening Librarian Menu...");
                LibrarianMenu.show(currentUser);
            }

            case "client" -> {
                System.out.println("Opening Client Menu...");
                ClientMenu.show(currentUser);
            }

            default -> {
                System.out.println("❌ Unknown role. Logging out...");
                currentUser = null;
            }
        }
    }
}
