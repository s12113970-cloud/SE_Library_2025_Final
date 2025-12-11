package org.example.library.services;

/**
 * A simple session manager used to track whether a user
 * is currently logged in or logged out.
 *
 * This service provides basic session state functionality:
 * <ul>
 *     <li>login()   → marks the session as active</li>
 *     <li>logout()  → marks the session as inactive</li>
 *     <li>isLoggedIn() → returns the current login state</li>
 * </ul>
 */


public class SessionService {

    private boolean loggedIn = false;

    /**
     * Marks the user session as active.
     */

    public void login() { loggedIn = true; }

    /**
     * Marks the user session as inactive.
     */

    public void logout() { loggedIn = false; }

    /**
     * Returns whether the user is currently logged in.
     *
     * @return true if logged in, false otherwise
     */

    public boolean isLoggedIn() { return loggedIn; }
}
