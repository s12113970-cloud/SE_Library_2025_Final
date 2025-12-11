package org.example.library.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    private EmailService emailService;

    @BeforeEach
    void setup() {
        emailService = new EmailService();
        // Clear email log before each test
        emailService.getEmailLog().clear();
    }

    // ==========================================================
    //          sendEmail() stores entry correctly
    // ==========================================================

    @Test
    void sendEmail_logsCorrectEntry() {
        emailService.sendEmail("test@mail.com", "Hello", "Body message");

        List<String> log = emailService.getEmailLog();

        assertEquals(1, log.size());
        assertTrue(log.get(0).contains("test@mail.com"));
        assertTrue(log.get(0).contains("Hello"));
        assertTrue(log.get(0).contains("Body message"));
    }

    // ==========================================================
    //          getEmailLog() returns empty when no email sent
    // ==========================================================

    @Test
    void getEmailLog_emptyInitially() {
        List<String> log = emailService.getEmailLog();
        assertEquals(0, log.size());
    }

    // ==========================================================
    //          Multiple emails stored correctly
    // ==========================================================

    @Test
    void sendEmail_multipleEntries() {
        emailService.sendEmail("a@mail.com", "S1", "M1");
        emailService.sendEmail("b@mail.com", "S2", "M2");

        List<String> log = emailService.getEmailLog();

        assertEquals(2, log.size());
        assertTrue(log.get(0).contains("a@mail.com"));
        assertTrue(log.get(1).contains("b@mail.com"));
    }
}
