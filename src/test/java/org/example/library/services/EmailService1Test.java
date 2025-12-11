package org.example.library.services;

import jakarta.mail.Transport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailService1Test {

    private EmailService1 service;

    @BeforeEach
    void setup() {
        service = new EmailService1();
        service.getEmailLog().clear(); // ensure fresh state
    }

    // =====================================================
    // Test: Successful mocked email send
    // =====================================================
    @Test
    void sendEmail_successfullyLogged() {

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            // Mock: Do nothing when Transport.send(...) is invoked
            transportMock.when(() -> Transport.send(any())).thenAnswer(inv -> null);

            service.sendEmail("user@mail.com", "Hello", "Test message");

            List<String> log = service.getEmailLog();

            assertEquals(1, log.size());
            assertTrue(log.get(0).contains("user@mail.com"));
            assertTrue(log.get(0).contains("Test message"));

            // ensure Transport.send was called exactly once
            transportMock.verify(() -> Transport.send(any()), times(1));
        }
    }

    // =====================================================
    // Test: Failing email (simulate exception)
    // =====================================================
    @Test
    void sendEmail_failureHandledGracefully() {

        try (MockedStatic<Transport> transportMock = mockStatic(Transport.class)) {

            // Simulate Transport.send throwing an exception
            transportMock.when(() -> Transport.send(any()))
                    .thenThrow(new RuntimeException("SMTP error"));

            // No exception should propagate outside
            assertDoesNotThrow(() ->
                    service.sendEmail("fail@mail.com", "Err", "Body")
            );

            // Log should NOT record a successful send
            assertEquals(0, service.getEmailLog().size());
        }
    }

    // =====================================================
    // Test: getEmailLog initially empty
    // =====================================================
    @Test
    void getEmailLog_initiallyEmpty() {
        assertEquals(0, service.getEmailLog().size());
    }
}
