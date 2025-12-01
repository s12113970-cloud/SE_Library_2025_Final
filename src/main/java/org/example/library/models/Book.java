package org.example.library.models;

import java.time.LocalDate;

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

    // Full constructor for Sprint 2
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
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() { return available; }
    public boolean isBorrowed() { return borrowed; }
    public LocalDate getDueDate() { return dueDate; }
    public double getFine() { return fine; }

    // Setters
    public void setBorrowed(boolean borrowed) { this.borrowed = borrowed; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }
    public void setFine(double fine) { this.fine = fine; }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
        if (this.quantity > 0) available = true;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= quantity) this.quantity -= amount;
        if (quantity == 0) available = false;
    }
}
