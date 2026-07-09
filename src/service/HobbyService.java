package service;

import db.DBConnection;
import model.Hobby;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;

public class HobbyService {

    public void addHobby(String title, String category, String reminderTime) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO hobbies(title, category, reminder_time, practiced, streak, last_practiced_date) VALUES (?, ?, ?, 0, 0, '')"
            );

            ps.setString(1, title);
            ps.setString(2, category);
            ps.setString(3, reminderTime);
            ps.executeUpdate();

            con.close();
            FileLogger.writeLog("Hobby added: " + title);
        } catch (Exception e) {
            System.out.println("Add Hobby Error: " + e.getMessage());
        }
    }

    public ArrayList<Hobby> getAllHobbies() {
        ArrayList<Hobby> hobbies = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement("SELECT * FROM hobbies");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Hobby h = new Hobby(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("category"),
                    rs.getString("reminder_time"),
                    rs.getInt("practiced") == 1,
                    rs.getInt("streak"),
                    rs.getString("last_practiced_date")
                );

                hobbies.add(h);
            }

            con.close();
        } catch (Exception e) {
            System.out.println("View Hobby Error: " + e.getMessage());
        }

        return hobbies;
    }

    public void markPracticed(int id) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement get = con.prepareStatement(
                "SELECT streak, last_practiced_date FROM hobbies WHERE id = ?"
            );

            get.setInt(1, id);
            ResultSet rs = get.executeQuery();

            int streak = 0;
            String lastDate = "";

            if (rs.next()) {
                streak = rs.getInt("streak");
                lastDate = rs.getString("last_practiced_date");
            }

            String today = LocalDate.now().toString();
            String yesterday = LocalDate.now().minusDays(1).toString();

            if (today.equals(lastDate)) {
                System.out.println("Already practiced today.");
                con.close();
                return;
            }

            if (yesterday.equals(lastDate)) {
                streak++;
            } else {
                streak = 1;
            }

            PreparedStatement ps = con.prepareStatement(
                "UPDATE hobbies SET practiced = 1, streak = ?, last_practiced_date = ? WHERE id = ?"
            );

            ps.setInt(1, streak);
            ps.setString(2, today);
            ps.setInt(3, id);
            ps.executeUpdate();

            con.close();
            FileLogger.writeLog("Hobby practiced with ID: " + id);
        } catch (Exception e) {
            System.out.println("Practice Hobby Error: " + e.getMessage());
        }
    }

    public void deleteHobby(int id) {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "DELETE FROM hobbies WHERE id = ?"
            );

            ps.setInt(1, id);
            ps.executeUpdate();

            con.close();
            FileLogger.writeLog("Hobby deleted with ID: " + id);
        } catch (Exception e) {
            System.out.println("Delete Hobby Error: " + e.getMessage());
        }
    }

    public void resetDailyHobbies() {
        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement get = con.prepareStatement("SELECT * FROM hobbies");
            ResultSet rs = get.executeQuery();

            String today = LocalDate.now().toString();

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                boolean practiced = rs.getInt("practiced") == 1;

                String status = practiced ? "Completed" : "Missed";

                PreparedStatement history = con.prepareStatement(
                    "INSERT INTO hobby_history(hobby_id, hobby_title, status, date) VALUES (?, ?, ?, ?)"
                );

                history.setInt(1, id);
                history.setString(2, title);
                history.setString(3, status);
                history.setString(4, today);
                history.executeUpdate();

                if (!practiced) {
                    PreparedStatement resetStreak = con.prepareStatement(
                        "UPDATE hobbies SET streak = 0 WHERE id = ?"
                    );

                    resetStreak.setInt(1, id);
                    resetStreak.executeUpdate();
                }
            }

            PreparedStatement reset = con.prepareStatement(
                "UPDATE hobbies SET practiced = 0"
            );

            reset.executeUpdate();

            con.close();
            FileLogger.writeLog("Daily reset completed");
        } catch (Exception e) {
            System.out.println("Daily Reset Error: " + e.getMessage());
        }
    }

    public ArrayList<String> getWeeklyProgress() {
        ArrayList<String> progress = new ArrayList<>();

        try {
            Connection con = DBConnection.getConnection();

            PreparedStatement ps = con.prepareStatement(
                "SELECT hobby_title, status, date FROM hobby_history " +
                "WHERE date >= date('now', '-7 days') ORDER BY date DESC"
            );

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                progress.add(
                    rs.getString("date") + " | " +
                    rs.getString("hobby_title") + " | " +
                    rs.getString("status")
                );
            }

            con.close();
        } catch (Exception e) {
            System.out.println("Weekly Progress Error: " + e.getMessage());
        }

        return progress;
    }
}