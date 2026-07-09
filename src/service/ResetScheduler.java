package service;

import java.time.LocalTime;

public class ResetScheduler extends Thread {
    private HobbyService hobbyService;

    public ResetScheduler(HobbyService hobbyService) {
        this.hobbyService = hobbyService;
    }

    @Override
    public void run() {
        boolean resetDone = false;

        while (true) {
            LocalTime now = LocalTime.now();

            if (now.getHour() == 0 && now.getMinute() == 0 && !resetDone) {
                hobbyService.resetDailyHobbies();
                resetDone = true;
            }

            if (now.getHour() == 0 && now.getMinute() == 1) {
                resetDone = false;
            }

            try {
                Thread.sleep(30000);
            } catch (Exception e) {
                System.out.println("Scheduler Error: " + e.getMessage());
            }
        }
    }
}