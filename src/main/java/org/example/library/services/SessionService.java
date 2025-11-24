package org.example.library.services;

public class SessionService {

    private boolean loggedIn = false;

    public void login() { loggedIn = true; }
    public void logout() { loggedIn = false; }
    public boolean isLoggedIn() { return loggedIn; }
}
