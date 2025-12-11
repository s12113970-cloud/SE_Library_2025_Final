package org.example.library.models;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class BookTest {

    @Test
    void testConstructorAndGetters() {
        Book b = new Book("A", "Author", "123", 5, true);

        assertEquals("A", b.getTitle());
        assertEquals("Author", b.getAuthor());
        assertEquals("123", b.getIsbn());
        assertEquals(5, b.getQuantity());
        assertTrue(b.isAvailable());
        assertFalse(b.isBorrowed());
        assertNull(b.getDueDate());
        assertEquals(0.0, b.getFine());
    }

    @Test
    void testFullConstructor() {
        LocalDate d = LocalDate.now();
        Book b = new Book("B", "Writer", "999", 2, false, true, d, 10.0);

        assertEquals("B", b.getTitle());
        assertEquals("Writer", b.getAuthor());
        assertEquals("999", b.getIsbn());
        assertEquals(2, b.getQuantity());
        assertFalse(b.isAvailable());
        assertTrue(b.isBorrowed());
        assertEquals(d, b.getDueDate());
        assertEquals(10.0, b.getFine());
    }

    @Test
    void testSetters() {
        Book b = new Book("A", "B", "C", 1, true);

        b.setBorrowed(true);
        b.setDueDate(LocalDate.of(2025, 1, 1));
        b.setFine(50);

        assertTrue(b.isBorrowed());
        assertEquals(LocalDate.of(2025, 1, 1), b.getDueDate());
        assertEquals(50, b.getFine());
    }

    @Test
    void testIncreaseQuantity() {
        Book b = new Book("A", "B", "C", 0, false);

        b.increaseQuantity(3);

        assertEquals(3, b.getQuantity());
        assertTrue(b.isAvailable());
    }

    @Test
    void testIncreaseQuantityZeroAmount() {
        Book b = new Book("A", "B", "C", 0, false);

        b.increaseQuantity(0); // quantity stays 0

        assertEquals(0, b.getQuantity());
        assertFalse(b.isAvailable());  // branch where available not set to true
    }


    @Test
    void testDecreaseQuantity() {
        Book b = new Book("A", "B", "C", 3, true);

        b.decreaseQuantity(2);
        assertEquals(1, b.getQuantity());
        assertTrue(b.isAvailable());

        b.decreaseQuantity(1);
        assertEquals(0, b.getQuantity());
        assertFalse(b.isAvailable());
    }

    @Test
    void testDecreaseQuantityInvalidAmount() {
        Book b = new Book("A", "B", "C", 5, true);

        b.decreaseQuantity(10);  // invalid (10 > 5) → should do nothing

        assertEquals(5, b.getQuantity());
        assertTrue(b.isAvailable());
    }

}
