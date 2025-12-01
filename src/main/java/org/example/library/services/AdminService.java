package org.example.library.services;

import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminService {

    // ===========================================
    //                  LOGIN
    // ===========================================
    public User login(String username, String password) {

        JSONObject db = FileDatabase.load();
        JSONArray users = db.getJSONArray("users");

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);

            if (u.getString("username").equals(username) &&
                    u.getString("password").equals(password)) {

                int id = u.has("id") ? u.getInt("id") : -1; // ⭐ ID only for clients

                return new User(
                        id,
                        u.getString("username"),
                        u.getString("password"),
                        u.getString("role")
                );
            }
        }
        return null;
    }

    // ===========================================
    //           UNREGISTER USER  (Sprint 4)
    // ===========================================
    public boolean unregisterUser(String usernameToRemove, User currentUser) {

        // 1) Only admins allowed
        if (!currentUser.isAdmin()) {
            System.out.println("❌ Only administrators can unregister users.");
            return false;
        }

        JSONObject db = FileDatabase.load();
        JSONArray users = db.getJSONArray("users");
        JSONArray books = db.getJSONArray("books");

        // 2) Find the target user + its index
        JSONObject targetUser = null;
        Integer targetId = null;
        int removeIndex = -1;

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);

            if (u.getString("username").equals(usernameToRemove)) {
                targetUser = u;
                removeIndex = i;

                if (u.has("id")) {
                    targetId = u.getInt("id"); // ⭐ only clients have IDs
                }
                break;
            }
        }

        if (targetUser == null) {
            System.out.println("❌ User not found.");
            return false;
        }

        // Admin cannot be deleted
        if (targetUser.getString("role").equals("admin")) {
            System.out.println("❌ You cannot unregister an admin account.");
            return false;
        }

        // 3) Check if this specific user has active loans or fines
        if (targetId != null) {
            for (int i = 0; i < books.length(); i++) {
                JSONObject b = books.getJSONObject(i);

                int borrowedBy = b.optInt("borrowedBy", -1);
                boolean borrowed = b.optBoolean("borrowed", false);
                double fine = b.optDouble("fine", 0);

                // If this client has borrowed books or fines
                if (borrowedBy == targetId) {

                    if (borrowed) {
                        System.out.println("❌ Cannot unregister user. They have active loans.");
                        return false;
                    }

                    if (fine > 0) {
                        System.out.println("❌ Cannot unregister user. They have unpaid fines.");
                        return false;
                    }
                }
            }
        }

        // 4) Remove using INDEX (correct way)
        users.remove(removeIndex);
        FileDatabase.save(db);

        System.out.println("✔ User '" + usernameToRemove + "' has been unregistered successfully.");
        return true;
    }
}
