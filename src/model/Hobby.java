package model;

public class Hobby extends Task {
    private String category;
    private String reminderTime;
    private int streak;
    private String lastPracticedDate;

    private static int hobbyCount = 0;

    public Hobby(int id, String title, String category, String reminderTime, boolean practiced, int streak, String lastPracticedDate) {
        super(id, title, practiced);
        this.category = category;
        this.reminderTime = reminderTime;
        this.streak = streak;
        this.lastPracticedDate = lastPracticedDate;
        hobbyCount++;
    }

    public String getCategory() {
        return category;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public int getStreak() {
        return streak;
    }

    public String getLastPracticedDate() {
        return lastPracticedDate;
    }

    public static int getHobbyCount() {
        return hobbyCount;
    }

    @Override
    public String getDetails() {
        return id + ". " + title + " | " + category + " | Reminder: " + reminderTime + " | Streak: " + streak + " | " + (practiced ? "Practiced" : "Not Practiced");
    }
}