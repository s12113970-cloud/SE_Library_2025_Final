package org.example.library.models;

public class Book {

    private String title;
    private String author;
    private String isbn;
    private int quantity;
    private boolean available;

    // For NEW books
    public Book(String title, String author, String isbn, int quantity) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.available = quantity > 0;
    }

    // For loading books from JSON
    public Book(String title, String author, String isbn, int quantity, boolean available) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.quantity = quantity;
        this.available = available;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() { return available; }

    public void increaseQuantity(int amount) {
        this.quantity += amount;
        if (this.quantity > 0) available = true;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= quantity) {
            this.quantity -= amount;
        }
        if (quantity == 0) available = false;
    }
}
