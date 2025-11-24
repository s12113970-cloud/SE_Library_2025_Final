package org.example.library.services;

import org.example.library.models.Book;
import org.example.library.storage.FileDatabase;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BookService {

    public JSONObject findBookByISBN(String isbn) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("isbn").equals(isbn)) {
                return b;
            }
        }
        return null;
    }

    public void addBook(Book book) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");

        JSONObject b = new JSONObject();
        b.put("title", book.getTitle());
        b.put("author", book.getAuthor());
        b.put("isbn", book.getIsbn());
        b.put("quantity", book.getQuantity());
        b.put("available", book.isAvailable());

        books.put(b);
        FileDatabase.save(db);
    }

    // ===== Search Functions =====

    public List<Book> searchByISBN(String isbn) {
        List<Book> results = new ArrayList<>();
        JSONObject b = findBookByISBN(isbn);
        if (b != null) {
            results.add(new Book(
                    b.getString("title"),
                    b.getString("author"),
                    b.getString("isbn"),
                    b.getInt("quantity"),
                    b.getBoolean("available")
            ));
        }
        return results;
    }

    public List<Book> searchByTitle(String title) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("title").toLowerCase().contains(title.toLowerCase())) {
                results.add(new Book(
                        b.getString("title"),
                        b.getString("author"),
                        b.getString("isbn"),
                        b.getInt("quantity"),
                        b.getBoolean("available")
                ));
            }
        }
        return results;
    }

    public List<Book> searchByAuthor(String author) {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            if (b.getString("author").toLowerCase().contains(author.toLowerCase())) {
                results.add(new Book(
                        b.getString("title"),
                        b.getString("author"),
                        b.getString("isbn"),
                        b.getInt("quantity"),
                        b.getBoolean("available")
                ));
            }
        }
        return results;
    }

    public List<Book> getAllBooks() {
        JSONObject db = FileDatabase.load();
        JSONArray books = db.getJSONArray("books");
        List<Book> results = new ArrayList<>();

        for (int i = 0; i < books.length(); i++) {
            JSONObject b = books.getJSONObject(i);
            results.add(new Book(
                    b.getString("title"),
                    b.getString("author"),
                    b.getString("isbn"),
                    b.getInt("quantity"),
                    b.getBoolean("available")
            ));
        }
        return results;
    }
}
