package org.example.library.models;

import java.time.LocalDate;

public class CD {

    private String id;
    private String title;
    private String artist;
    private int quantity;
    private boolean available;

    // Sprint 5 features
    private boolean borrowed;
    private LocalDate dueDate;
    private double fine;
    private int borrowedBy;

    public CD(String id, String title, String artist, int quantity, boolean available) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.quantity = quantity;
        this.available = available;

        this.borrowed = false;
        this.dueDate = null;
        this.fine = 0;
        this.borrowedBy = -1;
    }



    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getQuantity() { return quantity; }
    public boolean isAvailable() { return available; }
    public boolean isBorrowed() { return borrowed; }
    public LocalDate getDueDate() { return dueDate; }
    public double getFine() { return fine; }
    public int getBorrowedBy() { return borrowedBy; }



    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public void setBorrowedBy(int id) {
        this.borrowedBy = id;
    }
}
