package com.askwinston.helper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;

@Service
@EnableScheduling
@EnableAsync
@Slf4j
public class ScheduleService {

    private static final long FIXED_RATE_IN_MILLISECONDS = 1000L * 60L;

    @Data
    @AllArgsConstructor
    private static class ScheduledTask {
        private Runnable runnable;
        private Date executionDate;
    }

    private PriorityQueue<ScheduledTask> queue = new PriorityQueue<>(Comparator.comparing(ScheduledTask::getExecutionDate));

    public boolean scheduleTask(Runnable runnable, Date executionDate) {
        return queue.add(new ScheduledTask(runnable, executionDate));
    }

    public boolean scheduleTask(Runnable runnable, long delayInMilliseconds) {
        return queue.add(new ScheduledTask(runnable, Date.from(Instant.now().plusMillis(delayInMilliseconds))));
    }

    @Scheduled(fixedRate = FIXED_RATE_IN_MILLISECONDS)
    private void checkQueue() {
        queue.stream()
                .filter(task -> task.getExecutionDate().before(Date.from(Instant.now())))
                .forEach(task -> task.runnable.run());
        queue.removeIf(task -> task.getExecutionDate().before(Date.from(Instant.now())));
    }
}
