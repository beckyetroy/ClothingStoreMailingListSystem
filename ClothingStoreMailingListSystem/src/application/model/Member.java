package application.model;

import java.util.HashSet;
import java.util.Stack;

import java.time.*;

public class Member {

    private String email;
    private String username;
    private String password;
    private boolean isAdmin;
    private HashSet<String> groups = new HashSet<String>();
    private LocalDate signUpDate;
    private String lastPostedDate;

    public Member(String email, String username, String password, boolean isAdmin, HashSet<String> groups, String lastPostedDate) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.groups = groups;
        signUpDate = LocalDate.now();
        this.lastPostedDate = lastPostedDate;
    }

    public Member(String email, String username, String password, boolean isAdmin, HashSet groups) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
        this.groups = groups;
        signUpDate = LocalDate.now();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public HashSet getGroups() {
        return groups;
    }

    public void setGroups(HashSet groups) {
        this.groups = groups;
    }

    public LocalDate getSignUpDate() {
        return signUpDate;
    }

    public void setSignUpDate(LocalDate signUpDate) {
        this.signUpDate = signUpDate;
    }

    public String getLastPostedDate() {
        return lastPostedDate;
    }

    public void setLastPostedDate(String lastPostedDate) {
        this.lastPostedDate = lastPostedDate;
    }

    @Override
    public String toString() {
        return "Username: " + username +
                " | E-mail address: " + email +
                " | Password: " + password +
                " | Admin access (true/false): " + isAdmin +
                " | Groups Subscribed to: " + groups +
                " | Signed up: " + signUpDate +
                " | Last Posted: " + lastPostedDate;
    }
}
