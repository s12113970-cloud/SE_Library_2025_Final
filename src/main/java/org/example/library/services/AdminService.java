package org.example.library.services;

import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Service class that provides administrative operations such as logging in
 * and unregistering users from the library system.
 *
 * <p>This class interacts with the {@link FileDatabase} to read and update
 * stored user and book data.</p>
 */

public class AdminService {

    /**
     * Attempts to authenticate a user using a username and password.
     *
     * @param username the username entered by the user
     * @param password the password entered by the user
     * @return a {@link User} object if login is successful,
     *         or {@code null} if the credentials are invalid
     */

    public User login(String username, String password) {

        JSONObject db = FileDatabase.load();
        JSONArray users = db.getJSONArray("users");

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);

            if (u.getString("username").equals(username) &&
                    u.getString("password").equals(password)) {

                int id = u.has("id") ? u.getInt("id") : -1;

                String email = u.has("email") ? u.getString("email") : "unknown@mail.com";

                return new User(
                        id,
                        u.getString("username"),
                        u.getString("password"),
                        u.getString("role"),
                        email   // ⭐ NEW
                );
            }
        }
        return null;
    }


    /**
     * Removes a user from the system if the requester is an admin and
     * the target user has no active loans or unpaid fines.
     *
     * @param usernameToRemove username of the user to be removed
     * @param currentUser      the logged-in user performing the operation
     * @return {@code true} if removal succeeded,
     *         {@code false} if the user cannot be removed
     */

    public boolean unregisterUser(String usernameToRemove, User currentUser) {

        // Only admins may remove users
        if (!currentUser.isAdmin()) {
            System.out.println("❌ Only administrators can unregister users.");
            return false;
        }

        JSONObject db = FileDatabase.load();
        JSONArray users = db.getJSONArray("users");
        JSONArray books = db.getJSONArray("books");

        // Search for the user to remove
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

        // Admin accounts cannot be deleted
        if (targetUser.getString("role").equals("admin")) {
            System.out.println("❌ You cannot unregister an admin account.");
            return false;
        }

        // Check for active loans or unpaid fines
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

        // Remove user
        users.remove(removeIndex);
        FileDatabase.save(db);

        System.out.println("✔ User '" + usernameToRemove + "' has been unregistered successfully.");
        return true;
    }
}
