package org.example.library.services;

import org.example.library.models.CD;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class CDService {

    // ⭐ Helper: Convert JSON → CD object
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

    // ⭐ Add CD to database
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

    // ⭐ Get all CDs
    public List<CD> getAllCDs() {
        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        List<CD> results = new ArrayList<>();

        for (int i = 0; i < cds.length(); i++) {
            results.add(jsonToCD(cds.getJSONObject(i)));
        }

        return results;
    }

    // ⭐ Borrow CD (7 days)
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

    // ⭐ Overdue detection for CDs
    public void checkOverdueCDs() {
        JSONObject db = FileDatabase.load();
        JSONArray cds = db.getJSONArray("cds");

        LocalDate today = LocalDate.now();

        for (int i = 0; i < cds.length(); i++) {
            JSONObject c = cds.getJSONObject(i);

            if (c.optBoolean("borrowed", false)) {

                String due = c.optString("dueDate", null);

                if (due != null && !due.equals("null")) {
                    LocalDate dueDate = LocalDate.parse(due);
                    long daysLate = ChronoUnit.DAYS.between(dueDate, today);

                    if (daysLate > 0) {
                        double fine = daysLate * 20;  // ⭐ CD fine rule = 20 NIS per day
                        c.put("fine", fine);
                    }
                }
            }
        }

        FileDatabase.save(db);
        System.out.println("✔ CD overdue detection complete.");
    }

    // ⭐ Total fine for THIS user’s CDs
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
