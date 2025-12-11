package org.example.library.models;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class CDTest {

    @Test
    void testCDConstructorAndGetters() {
        CD cd = new CD("1", "Best Hits", "Artist A", 5, true);

        assertEquals("1", cd.getId());
        assertEquals("Best Hits", cd.getTitle());
        assertEquals("Artist A", cd.getArtist());
        assertEquals(5, cd.getQuantity());
        assertTrue(cd.isAvailable());

        assertFalse(cd.isBorrowed());
        assertNull(cd.getDueDate());
        assertEquals(0, cd.getFine());
        assertEquals(-1, cd.getBorrowedBy());
    }

    @Test
    void testSetBorrowed() {
        CD cd = new CD("1", "Album", "Artist", 2, true);

        cd.setBorrowed(true);
        assertTrue(cd.isBorrowed());

        cd.setBorrowed(false);
        assertFalse(cd.isBorrowed());
    }

    @Test
    void testSetDueDate() {
        CD cd = new CD("1", "Album", "Artist", 2, true);
        LocalDate date = LocalDate.now().plusDays(5);

        cd.setDueDate(date);
        assertEquals(date, cd.getDueDate());
    }

    @Test
    void testSetFine() {
        CD cd = new CD("1", "Album", "Artist", 2, true);

        cd.setFine(25.5);
        assertEquals(25.5, cd.getFine());
    }

    @Test
    void testSetBorrowedBy() {
        CD cd = new CD("1", "Album", "Artist", 2, true);

        cd.setBorrowedBy(10);
        assertEquals(10, cd.getBorrowedBy());
    }
}
