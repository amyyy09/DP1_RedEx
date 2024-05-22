package src.scheduled;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {

    @Scheduled(fixedRate = 5000)
    public void performTask() {
        System.out.println("Regular task performed using Spring Scheduler.");
    }
}
