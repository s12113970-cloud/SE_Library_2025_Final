package org.example.library.services;

import java.util.ArrayList;
import java.util.List;

/**
 * A mock email service used for testing the library system.
 * <p>
 * Instead of sending real emails, this service stores email details
 * in a static in-memory list so tests can verify the email contents.
 * </p>
 *
 * Features:
 * <ul>
 *     <li>sendEmail() → stores a formatted email log entry</li>
 *     <li>getEmailLog() → returns all stored email entries</li>
 * </ul>
 */


public class EmailService {

    private static final List<String> emailLog = new ArrayList<>();

    /**
     * Simulates sending an email by storing the formatted email entry
     * into an internal static log. No real email is sent -> Mock email
     *
     * @param to email recipient
     * @param subject email subject
     * @param message email body
     */

    public void sendEmail(String to, String subject, String message) {
        String entry = "TO: " + to + " | SUBJECT: " + subject + " | MESSAGE: " + message;
        emailLog.add(entry);

        System.out.println("📧 Mock email sent → " + entry);
    }

    /**
     * Returns all stored (mocked) email messages.
     *
     * @return list of log entries that represent sent emails
     */

    public List<String> getEmailLog() {
        return emailLog;
    }
}
