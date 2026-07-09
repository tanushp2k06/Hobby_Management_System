package app;

import db.DBConnection;
import server.HobbyServer;
import service.HobbyService;
import service.ResetScheduler;

public class Main {
    public static void main(String[] args) {
        DBConnection.initializeDatabase();

        HobbyService service = new HobbyService();
        ResetScheduler scheduler = new ResetScheduler(service);
        scheduler.start();

        HobbyServer server = new HobbyServer();
        server.startServer();
    }
}