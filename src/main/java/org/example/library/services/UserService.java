package org.example.library.services;

import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for retrieving all users stored in the JSON database.
 * <p>
 * This service reads from {@link FileDatabase}, extracts user fields,
 * and converts each JSON object into a {@link User} instance.
 * </p>
 *
 * Features:
 * <ul>
 *     <li>Loads all users from the "users" array in the database</li>
 *     <li>Constructs User objects with ID, username, password, role, and email</li>
 * </ul>
 */


public class UserService {

    /**
     * Retrieves all users from the database and converts them into model objects.
     *
     * @return list of {@link User} instances representing all stored users
     */


    public List<User> getAllUsers() {

        JSONObject db = FileDatabase.load();
        JSONArray arr = db.getJSONArray("users");

        List<User> users = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONObject u = arr.getJSONObject(i);

            users.add(new User(
                    u.getInt("id"),
                    u.getString("username"),
                    u.getString("password"),
                    u.getString("role"),
                    u.getString("email")   // ⭐ NEW
            ));
        }

        return users;
    }
}
