package model;

public abstract class Task {
    protected int id;
    protected String title;
    protected boolean practiced;

    public Task(int id, String title, boolean practiced) {
        this.id = id;
        this.title = title;
        this.practiced = practiced;
    }

    public abstract String getDetails();

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isPracticed() {
        return practiced;
    }
}