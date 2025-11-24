package org.example.library.services;

import org.example.library.models.User;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

public class AdminService {

    public User login(String username, String password) {
        JSONObject db = FileDatabase.load();
        JSONArray users = db.getJSONArray("users");

        for (int i = 0; i < users.length(); i++) {
            JSONObject u = users.getJSONObject(i);

            if (u.getString("username").equals(username) &&
                    u.getString("password").equals(password)) {

                return new User(username, password, u.getString("role"));
            }
        }
        return null;
    }
}
