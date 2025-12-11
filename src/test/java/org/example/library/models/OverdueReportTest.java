package org.example.library.models;

import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverdueReportTest {

    @Test
    void testConstructorAndGetters() {
        User user = new User(1, "dima", "123", "client", "dima@example.com");
        JSONObject item1 = new JSONObject().put("isbn", "111");
        JSONObject item2 = new JSONObject().put("isbn", "222");

        OverdueReport report = new OverdueReport(user, List.of(item1, item2));

        assertEquals(user, report.getUser());
        assertEquals(2, report.getCount());
        assertEquals(List.of(item1, item2), report.getItems());
    }

    @Test
    void testToStringFormat() {
        User user = new User(2, "ahmad", "pass", "client", "ahmad@example.com");
        OverdueReport report = new OverdueReport(user, List.of(new JSONObject()));

        String expected = "OverdueReport{ user=ahmad, overdueCount=1 }";

        assertEquals(expected, report.toString());
    }
}
