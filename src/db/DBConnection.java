package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DBConnection {
    private static final String URL = "jdbc:sqlite:hobby.db";

    public static Connection getConnection() throws Exception {
        return DriverManager.getConnection(URL);
    }

    public static void initializeDatabase() {
        try {
            Connection con = getConnection();
            Statement st = con.createStatement();

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS hobbies (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT NOT NULL, " +
                "category TEXT NOT NULL, " +
                "reminder_time TEXT, " +
                "practiced INTEGER DEFAULT 0, " +
                "streak INTEGER DEFAULT 0, " +
                "last_practiced_date TEXT)"
            );

            st.executeUpdate(
                "CREATE TABLE IF NOT EXISTS hobby_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "hobby_id INTEGER, " +
                "hobby_title TEXT, " +
                "status TEXT, " +
                "date TEXT)"
            );

            con.close();
        } catch (Exception e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }
}