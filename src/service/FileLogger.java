package service;

import java.io.FileWriter;
import java.time.LocalDateTime;

public class FileLogger {
    public static synchronized void writeLog(String message) {
        try {
            FileWriter fw = new FileWriter("activity_log.txt", true);
            fw.write(LocalDateTime.now() + " - " + message + "\n");
            fw.close();
        } catch (Exception e) {
            System.out.println("Log Error: " + e.getMessage());
        }
    }
}