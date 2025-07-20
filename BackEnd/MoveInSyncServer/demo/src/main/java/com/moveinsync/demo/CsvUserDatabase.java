package com.moveinsync.demo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CsvUserDatabase {
    public static class UserData {
        private String email;
        private String name;
        private String password;

        public UserData(String username, String name, String password) {
            this.email = username;
            this.name = name;
            this.password = password;
        }
        public String getUsername() { return email; }
        public void setUsername(String username) { this.email = username; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    private static final String CSV_FILE = "databases/users.csv";
    // Map by username (unique)
    private static final Map<String, UserData> userMap = new HashMap<>();
    private static boolean loaded = false;

    // Call this method on application startup to load users into cache
    public static void initialize() {
        loadUsers();
    }

    public static synchronized boolean addUser(String username, String name, String password) {
        if (userMap.containsKey(username)) {
            return false; // user already exists
        }
        try (java.io.FileWriter fw = new java.io.FileWriter(CSV_FILE, true)) {
            fw.write(username + "," + password + "," + name + "\n");
            userMap.put(username, new UserData(username, password, name));
            loadUsers();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void loadUsers() {
        if (loaded) return;
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            boolean firstLine = true;
            while ((line = br.readLine()) != null) {
                if (firstLine) { firstLine = false; continue; } // skip header
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String username = parts[0].trim();
                    String name = parts[1].trim();
                    String password = parts[2].trim();
                    userMap.put(username, new UserData(username, name, password));
                }
            }
            loaded = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean validateUser(String username, String password) {
        UserData user = userMap.get(username);
        return user != null && password.equals(user.getPassword());
    }

    public static UserData getUser(String username) {
        return userMap.get(username);
    }
}
