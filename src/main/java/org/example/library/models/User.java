package org.example.library.models;

/**
 * Represents a user in the library system.
 * A user can have different roles such as admin, librarian, or regular user.
 * Each user has a unique ID, username, password, role, and email address.
 */

public class User {

    private int id;            // ⭐ NEW
    private String username;
    private String password;
    private String role;
    private String email;

    /**
     * Constructs a new User object.
     *
     * @param id       the unique identifier for the user
     * @param username the username used for login
     * @param password the user's password
     * @param role     the role assigned to the user (admin, librarian, user)
     * @param email    the user's email address
     */

    // Constructor with ID
    public User(int id, String username, String password, String role, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    /**
     * Returns the user's email address.
     *
     * @return the email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the unique identifier of the user.
     *
     * @return the user's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the username of the user.
     *
     * @return the user's username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the password of the user.
     * (Note: In real systems, passwords should never be stored or returned as plain text.)
     *
     * @return the user's password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Returns the role of the user.
     *
     * @return the user's role as a string
     */
    public String getRole() {
        return role;
    }

    /**
     * Checks whether the provided password matches the user's password.
     *
     * @param pass the password to compare with
     * @return true if the password matches, false otherwise
     */
    public boolean checkPassword(String pass) {
        return password.equals(pass);
    }

    /**
     * Determines whether the user has an admin role.
     *
     * @return true if the user's role is "admin" (case-insensitive), false otherwise
     */
    public boolean isAdmin() {
        return role.equalsIgnoreCase("admin");
    }}
