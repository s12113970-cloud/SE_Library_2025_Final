package org.example.library.services;

import jakarta.mail.*;
import jakarta.mail.internet.*;

import java.util.*;

/**
 * A real HTML email service that sends messages using Gmail SMTP.
 * <p>
 * This service builds a styled HTML email template and sends it
 * using Jakarta Mail. It also stores a log entry for testing purposes.
 * </p>
 *
 * Features:
 * <ul>
 *     <li>HTML email rendering</li>
 *     <li>Gmail SMTP authentication</li>
 *     <li>App-password based login</li>
 *     <li>Email logging for verification</li>
 * </ul>
 */


public class EmailService1 {

    private static final List<String> emailLog = new ArrayList<>();

    private final String sender = "deemahamdan2004@gmail.com";  // إيميلك
    private final String appPassword = "vuyk iyob vgns aypo\n";     // ضعي App Password هنا فقط


    /**
     * Sends an HTML email using Gmail SMTP with TLS encryption.
     * <p>
     * The method constructs a styled HTML template, configures
     * SMTP properties, authenticates using the sender's app password,
     * and sends the message through Jakarta Mail.
     * </p>
     *
     * If sending succeeds, the message is recorded in an internal log.
     * If sending fails, the exception is caught and printed.
     *
     * @param recipient target email address
     * @param subject email subject line
     * @param message main message body to embed inside the HTML template
     */


    public void sendEmail(String recipient, String subject, String message) {

        Properties props = new Properties();
        props.put("mail.smtp.auth", true);
        props.put("mail.smtp.starttls.enable", true);
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, appPassword);
                    }
                }
        );

        try {

            String html =
                    "<div style='width: 100%; padding: 20px; background: #f2f2f2; font-family: Arial;'>"
                            + "<div style='max-width: 500px; margin: auto; background: white; padding: 25px; "
                            + "border-radius: 12px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'>"

                            + "<h2 style='text-align:center; color:#4C6EF5;'>📚 Library Reminder</h2>"
                            + "<p style='font-size:16px; color:#333;'>Hello,</p>"

                            + "<p style='font-size:16px; color:#333;'>"
                            + message
                            + "</p>"

                            + "<div style='margin-top:20px; padding:12px; background:#eef3ff; border-radius:8px;'>"
                            + "<p style='margin:0; font-size:14px; color:#4C6EF5;'>"
                            + "Please return overdue items as soon as possible."
                            + "</p>"
                            + "</div>"

                            + "<p style='margin-top:25px; text-align:center; font-size:12px; color:#777;'>"
                            + "This is an automated message from the Library System."
                            + "</p>"

                            + "</div></div>";

            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(sender, "Library System"));
            msg.setRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            msg.setSubject(subject);

            msg.setContent(html, "text/html; charset=utf-8");

            Transport.send(msg);

            emailLog.add("Sent to " + recipient + ": " + message);
            System.out.println("📨 HTML Email sent to " + recipient);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("❌ Failed to send HTML email.");
        }
    }

    /**
     * Returns a list of all successfully logged email entries.
     *
     * @return list of formatted strings representing sent emails
     */


    public List<String> getEmailLog() {
        return emailLog;
    }
}
