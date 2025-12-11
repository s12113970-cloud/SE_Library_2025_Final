package org.example.library.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmailMessageTest {

    @Test
    void testConstructorAndGetters() {
        EmailMessage msg = new EmailMessage("user@mail.com", "Reminder", "You have overdue books");

        assertEquals("user@mail.com", msg.getTo());
        assertEquals("Reminder", msg.getSubject());
        assertEquals("You have overdue books", msg.getBody());
    }

    @Test
    void testToStringFormat() {
        EmailMessage msg = new EmailMessage("test@mail.com", "Hello", "Body text");

        String expected = "EmailMessage{ to='test@mail.com', subject='Hello', body='Body text' }";

        assertEquals(expected, msg.toString());
    }
}
