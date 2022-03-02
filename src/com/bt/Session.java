package com.bt;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public class Session {

    private String user;
    private LocalTime startTime;
    private LocalTime endTime;

    public Session(String user, String startTime, String endTime) {
        this.user = user;
        this.startTime = LocalTime.parse(startTime);
        this.endTime = LocalTime.parse(endTime);
    }

    public String getUser() {
        return user;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public long calculateSessionLength() {
        return startTime.until(endTime, ChronoUnit.SECONDS);
    }
}
