package org.example.library.services;

import org.example.library.models.CD;
import org.example.library.storage.FileDatabase;
import org.example.library.strategies.FineStrategy;
import org.example.library.strategies.CDFineStrategy;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for managing CD operations in the library system.
 * This includes:
 * <ul>
 *     <li>Adding new CDs to the database</li>
 *     <li>Retrieving CDs</li>
 *     <li>Borrowing CDs (7-day loan)</li>
 *     <li>Detecting overdue CDs and applying fines (Strategy Pattern)</li>
 *     <li>Calculating total CD fines for a specific user</li>
 * </ul>
 *
 * This class interacts with {@link FileDatabase}, and applies
 * {@link CDFineStrategy} for overdue fine calculation.
 */


public class CDService {


    /**
     * Converts a JSONObject representing a CD into a CD model object.
     *
     * @param c JSONObject containing CD fields from the database
     * @return CD object with correctly mapped fields
     */

    private CD jsonToCD(JSONObject c) {
        CD cd = new CD(
                c.getString("id"),
                c.getString("title"),
                c.getString("artist"),
                c.getInt("quantity"),
                c.getBoolean("available")
        );

        if (c.has("borrowed"))
            cd.setBorrowed(c.getBoolean("borrowed"));

        if (c.has("dueDate") && !c.isNull("dueDate"))
            cd.setDueDate(LocalDate.parse(c.getString("dueDate")));

        if (c.has("fine"))
            cd.setFine(c.getDouble("fine"));

        if (c.has("borrowedBy"))
            cd.setBorrowedBy(c.optInt("borrowedBy", -1));

        return cd;
    }

    /**
     * Adds a new CD to the JSON database.
     *
     * Fields initialized:
     * <ul>
     *     <li>borrowed = false</li>
     *     <li>dueDate = null</li>
     *     <li>fine = 0</li>
     * </ul>
     *
     * @param cd CD object to be stored
     */

    public void addCD(CD cd) {
        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        JSONObject c = new JSONObject();
        c.put("id", cd.getId());
        c.put("title", cd.getTitle());
        c.put("artist", cd.getArtist());
        c.put("quantity", cd.getQuantity());
        c.put("available", cd.isAvailable());
        c.put("borrowed", false);
        c.put("dueDate", JSONObject.NULL);
        c.put("fine", 0);
        c.put("borrowedBy", JSONObject.NULL);

        cds.put(c);
        FileDatabase.save(db);
    }

    /**
     * Retrieves all CDs stored in the database.
     *
     * @return list of CD objects
     */

    public List<CD> getAllCDs() {
        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        List<CD> results = new ArrayList<>();

        for (int i = 0; i < cds.length(); i++) {
            results.add(jsonToCD(cds.getJSONObject(i)));
        }

        return results;
    }

    /**
     * Allows a user to borrow a CD for 7 days.
     * Updates:
     * <ul>
     *     <li>quantity--</li>
     *     <li>available = false (if quantity reaches 0)</li>
     *     <li>borrowed = true</li>
     *     <li>borrowedBy = user's ID</li>
     *     <li>dueDate = today + 7 days</li>
     * </ul>
     *
     * Prints messages for:
     * <ul>
     *     <li>CD not available</li>
     *     <li>CD already borrowed</li>
     *     <li>CD not found</li>
     * </ul>
     *
     * @param id CD identifier
     * @param currentUser user borrowing the CD
     */

    public void borrowCD(String id, org.example.library.models.User currentUser) {
        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        for (int i = 0; i < cds.length(); i++) {
            JSONObject c = cds.getJSONObject(i);

            if (c.getString("id").equals(id)) {

                if (!c.getBoolean("available")) {
                    System.out.println("❌ CD is not available.");
                    return;
                }

                if (c.getBoolean("borrowed")) {
                    System.out.println("❌ CD already borrowed.");
                    return;
                }

                // Update quantity
                int qty = c.getInt("quantity");
                c.put("quantity", qty - 1);
                if (qty - 1 == 0) c.put("available", false);

                // Borrow Info
                c.put("borrowed", true);
                c.put("borrowedBy", currentUser.getId());
                c.put("dueDate", LocalDate.now().plusDays(7).toString());
                c.put("fine", 0);

                FileDatabase.save(db);
                System.out.println("🎵 CD Borrowed! Due date: " + c.getString("dueDate"));
                return;
            }
        }

        System.out.println("❌ CD not found.");
    }

    /**
     * Checks all CDs to determine whether they are overdue.
     * If overdue:
     * <ul>
     *     <li>Fine is calculated using {@link CDFineStrategy}</li>
     *     <li>Fine is stored in database</li>
     * </ul>
     *
     * Criteria:
     * <ul>
     *     <li>borrowed == true</li>
     *     <li>dueDate exists</li>
     *     <li>daysLate > 0</li>
     * </ul>
     */

    public void checkOverdueCDs() {

        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        LocalDate today = LocalDate.now();

        // ⭐ Use CD Fine Strategy
        FineStrategy strategy = new CDFineStrategy();

        for (int i = 0; i < cds.length(); i++) {
            JSONObject c = cds.getJSONObject(i);

            if (c.optBoolean("borrowed", false)) {

                String due = c.optString("dueDate", null);

                if (due != null && !due.equals("null")) {
                    LocalDate dueDate = LocalDate.parse(due);
                    long daysLate = ChronoUnit.DAYS.between(dueDate, today);

                    if (daysLate > 0) {

                        // ⭐ Calculate fine using STRATEGY
                        double fine = strategy.calculateFine((int) daysLate);
                        c.put("fine", fine);
                    }
                }
            }
        }

        FileDatabase.save(db);
        System.out.println("✔ CD overdue detection complete (Strategy Pattern applied).");
    }


    /**
     * Computes the total CD fines for a specific user.
     *
     * @param userId user ID to lookup
     * @return total fine value in double
     */

    public double getTotalCdFineForUser(int userId) {
        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        double total = 0;

        for (int i = 0; i < cds.length(); i++) {
            JSONObject c = cds.getJSONObject(i);

            if (c.optInt("borrowedBy", -1) == userId) {
                total += c.optDouble("fine", 0);
            }
        }

        return total;
    }
}
