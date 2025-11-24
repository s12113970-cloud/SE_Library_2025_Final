package org.example.library.storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileDatabase {

    private static String PATH = System.getProperty("user.dir") + "/library.json";

    public static void useMainDatabase() {
        PATH = System.getProperty("user.dir") + "/library.json";
    }

    public static void useTestDatabase() {
        PATH = System.getProperty("user.dir") + "/test_library.json";
    }

    public static JSONObject load() {
        try {
            if (!Files.exists(Paths.get(PATH))) {
                JSONObject empty = new JSONObject();
                empty.put("users", new JSONArray());
                empty.put("books", new JSONArray());
                empty.put("loans", new JSONArray());
                save(empty);
            }

            String content = Files.readString(Paths.get(PATH));
            return new JSONObject(content);

        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON file", e);
        }
    }

    public static void save(JSONObject json) {
        try {
            Files.writeString(Paths.get(PATH), json.toString(4));
        } catch (Exception e) {
            throw new RuntimeException("Error saving JSON file", e);
        }
    }

    public static void reset() {
        JSONObject empty = new JSONObject();
        empty.put("users", new JSONArray());
        empty.put("books", new JSONArray());
        empty.put("loans", new JSONArray());
        save(empty);
    }
}
