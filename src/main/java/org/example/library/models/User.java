package org.example.library.models;

public class User {

    private int id;            // ⭐ NEW
    private String username;
    private String password;
    private String role;

    // Constructor with ID
    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getter for ID
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean checkPassword(String pass) {
        return password.equals(pass);
    }

    public boolean isAdmin() {
        return role.equalsIgnoreCase("admin");
    }
}
