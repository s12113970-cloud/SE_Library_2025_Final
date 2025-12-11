package org.example.library.models;

import java.time.LocalDate;

/**
 * Represents a book in the library system.
 * Stores metadata such as title, author, ISBN, availability,
 * borrowing status, due dates, and fines.
 *
 * @author Dima & Asma'a
 * @version 1.0
 */

public class Book {

    private String title;
    private String author;
    private String isbn;

    private int quantity;
    private boolean available;

    // Sprint 2 fields
    private boolean borrowed;
    private LocalDate dueDate;
    private double fine;

    /**
     * Basic constructor used when loading books from JSON.
     *
     * @param title     the book title
     * @param author    the author's name
     * @param isbn      ISBN identifier
     * @param quantity  number of copies available
     * @param available whether the book is available for borrowing
     */

    // Constructor for JSON loading (Sprint 1 style)
    public Book(String title, String author, String isbn, int quantity, boolean available) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.available = available;

        // Sprint 2 defaults
        this.borrowed = false;
        this.dueDate = null;
        this.fine = 0.0;
    }

    /**
     * Full constructor including all Sprint 2 fields.
     *
     * @param title     book title
     * @param author    author name
     * @param isbn      ISBN
     * @param quantity  number of copies
     * @param available whether book is available
     * @param borrowed  borrowing status
     * @param dueDate   due date for return
     * @param fine      current fine amount
     */

    public Book(String title, String author, String isbn, int quantity, boolean available,
                boolean borrowed, LocalDate dueDate, double fine) {

        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.available = available;
        this.borrowed = borrowed;
        this.dueDate = dueDate;
        this.fine = fine;
    }

    // Getters
    /** @return the book title */
    public String getTitle() { return title; }

    /** @return the author name */
    public String getAuthor() { return author; }

    /** @return the ISBN */
    public String getIsbn() { return isbn; }

    /** @return available quantity */
    public int getQuantity() { return quantity; }

    /** @return true if book is available */
    public boolean isAvailable() { return available; }

    /** @return true if book is borrowed */
    public boolean isBorrowed() { return borrowed; }

    /** @return due date for return */
    public LocalDate getDueDate() { return dueDate; }

    /** @return fine amount */
    public double getFine() { return fine; }

    // Setters
    /**
     * Updates the borrowing status.
     * @param borrowed true if a user borrowed the book
     */
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }

    /**
     * Sets the due date for a borrowed book.
     * @param dueDate expected return date
     */
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /**
     * Updates the fine amount for this book.
     * @param fine new fine value
     */
    public void setFine(double fine) { this.fine = fine; }


    // =================== Quantity logic ===================

    /**
     * Increases available quantity.
     * Automatically sets available = true if quantity > 0.
     *
     * @param amount number of copies to add
     */
    public void increaseQuantity(int amount) {
        this.quantity += amount;
        if (this.quantity > 0) available = true;
    }

    /**
     * Decreases the book quantity safely.
     * If quantity reaches zero, book becomes unavailable.
     *
     * @param amount number of copies to remove (must be <= quantity)
     */
    public void decreaseQuantity(int amount) {
        if (amount <= quantity) this.quantity -= amount;
        if (quantity == 0) available = false;
    }


}
