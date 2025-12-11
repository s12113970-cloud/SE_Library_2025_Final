package org.example.library.models;

/**
 * Represents an email message used by the library's notification system.
 * This class is used together with the Observer Pattern to send
 * overdue reminders or other system notifications.
 *
 * @author Dima
 * @version 1.0
 */
public class EmailMessage {

    /** Recipient email address */
    private final String to;

    /** Email subject */
    private final String subject;

    /** Email body content */
    private final String body;

    /**
     * Creates a new email message.
     *
     * @param to      Recipient email address
     * @param subject Email subject line
     * @param body    Body content of the email
     */
    public EmailMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    /** @return email recipient */
    public String getTo() { return to; }

    /** @return email subject */
    public String getSubject() { return subject; }

    /** @return email body text */
    public String getBody() { return body; }

    /**
     * Converts this message into a readable string.
     *
     * @return formatted message
     */
    @Override
    public String toString() {
        return "EmailMessage{ to='" + to + "', subject='" + subject + "', body='" + body + "' }";
    }
}
/////dima hamdan