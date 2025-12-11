package org.example.library.models;

import org.json.JSONObject;
import java.util.List;

/**
 * Represents a report of overdue items for a specific user.
 * The report contains:
 *  - The user who owns the overdue items
 *  - A list of JSON objects representing each overdue item
 *
 * This model is used in reporting features (Sprint 5) to display
 * a combined summary of all overdue books and CDs.
 *
 * @author Dima
 * @version 1.0
 */
public class OverdueReport {

    /** User who owns the overdue items */
    private final User user;

    /** List of overdue items (books or CDs) stored as JSON objects */
    private final List<JSONObject> overdueItems;

    /**
     * Creates a new overdue report for a user.
     *
     * @param user         the user who has overdue items
     * @param overdueItems list of overdue JSON objects
     */
    public OverdueReport(User user, List<JSONObject> overdueItems) {
        this.user = user;
        this.overdueItems = overdueItems;
    }

    /**
     * @return the user who owns this report
     */
    public User getUser() { return user; }

    /**
     * @return number of overdue items
     */
    public int getCount() { return overdueItems.size(); }

    /**
     * @return list of overdue item objects
     */
    public List<JSONObject> getItems() { return overdueItems; }

    /**
     * String representation used for logging/report summaries.
     *
     * @return formatted text summary
     */
    @Override
    public String toString() {
        return "OverdueReport{ user=" + user.getUsername() +
                ", overdueCount=" + overdueItems.size() + " }";
    }
}
