package com.bt;

public class UserSummary {

    private String user;
    private int sessionCount;
    private long totalSessionLength;

    public UserSummary(String user, int sessionCount, long totalSessionLength) {
        this.user = user;
        this.sessionCount = sessionCount;
        this.totalSessionLength = totalSessionLength;
    }

    public String getUser() {
        return user;
    }

    public int getSessionCount() {
        return sessionCount;
    }

    public long getTotalSessionLength() {
        return totalSessionLength;
    }

    public void setSessionCount(int sessionCount) {
        this.sessionCount = sessionCount;
    }

    public void setTotalSessionLength(long totalSessionLength) {
        this.totalSessionLength = totalSessionLength;
    }
}
