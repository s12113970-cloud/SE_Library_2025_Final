package org.example.library.models;

public class User {
    private String username;
    private String password;
    private String role;

    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String pass) { return password.equals(pass); }
    public boolean isAdmin() { return role.equals("admin"); }
}
