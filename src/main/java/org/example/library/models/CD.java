package org.example.library.models;

import java.time.LocalDate;

/**
 * Represents a CD item in the library system.
 * This model is used in Sprint 5 to support borrowing,
 * overdue detection, and fine calculation for multimedia items.
 *
 * @author Dima & Asma'a
 * @version 1.0
 */

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

    /**
     * Constructor used when loading or creating a CD.
     *
     * @param id        Unique CD identifier
     * @param title     Title of the CD
     * @param artist    Artist name
     * @param quantity  Number of copies available
     * @param available Whether the CD is available for borrowing
     */

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



    /** @return CD ID */
    public String getId() { return id; }

    /** @return CD title */
    public String getTitle() { return title; }

    /** @return artist name */
    public String getArtist() { return artist; }

    /** @return CD quantity */
    public int getQuantity() { return quantity; }

    /** @return whether the CD is available */
    public boolean isAvailable() { return available; }

    /** @return whether the CD is borrowed */
    public boolean isBorrowed() { return borrowed; }

    /** @return due date of return */
    public LocalDate getDueDate() { return dueDate; }

    /** @return fine amount */
    public double getFine() { return fine; }

    /** @return ID of user who borrowed the CD */
    public int getBorrowedBy() { return borrowedBy; }


    /**
     * Marks the CD as borrowed or returned.
     *
     * @param borrowed true if borrowed, false if returned
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    /**
     * Sets the due date for returning the CD.
     *
     * @param dueDate LocalDate of due date
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Updates the fine amount for this CD.
     *
     * @param fine fine in NIS
     */
    public void setFine(double fine) {
        this.fine = fine;
    }

    /**
     * Sets the user ID who borrowed the CD.
     *
     * @param id user ID
     */
    public void setBorrowedBy(int id) {
        this.borrowedBy = id;
    }
}
