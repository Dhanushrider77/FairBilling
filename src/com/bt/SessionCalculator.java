package com.bt;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SessionCalculator {
    // regular expression representing the expected log line format
    private static final String LOGFORMAT = "\\d\\d:\\d\\d:\\d\\d\\s\\w+\\s(Start|End)";

    public void printSessionData(String fileLocation) throws IOException {
        // This variable will keep track of the lines that have been processed so they are not read twice
        List<Integer> processedLines = new ArrayList<>();

        // To keep track of user sessions
        List<Session> sessions = new ArrayList<>();

        Path path = Paths.get(fileLocation); // convert to path because it is required by readAllLines method below
        // Get all lines in file as a List
        List<String> lines = Files.readAllLines(path);

        // Get the earliest total time from the log file and the latest total time. Required by logic later.
        String earliestTime = findEarliestTime(lines);
        String finishedTime = findFinishedTime(lines);

        // Read each line one at a time
        for (int lineNumber = 0; lineNumber < lines.size(); lineNumber++) {
            String line = lines.get(lineNumber);

            if(!line.matches(LOGFORMAT)) {
                // Ignore all lines that do not match the expected log line format
                continue;
            }

            // Extract the elements of interest
            String[] elements = getElements(line);
            String time = elements[0];
            String user = elements[1];
            String position = elements[2];

            if (position.equals("Start")) {
                String startTime = time;
                // Find the end time for this user session
                String endTime = findNextEndPosition(lines, user, lineNumber, processedLines, finishedTime);
                Session session = new Session(user, startTime, endTime);
                sessions.add(session);
            } else if (!processedLines.contains(lineNumber)) {
                // Getting to this point means the line is of End position and that it has not been processed before.
                // If an End line has not been processed before this means it did not have a corressponding start
                // so we should use the total earliest time as the start time
                String startTime = earliestTime;
                String endTime = time;
                Session session = new Session(user, startTime, endTime);
                sessions.add(session);
            }
        }

        // Create a map where key=username and value=sumary object. Iterate the sessions and populate the map with summary objects.
        Map<String, UserSummary> sessionSummaries = new HashMap<>();
        for (Session session : sessions) {

            String user = session.getUser();

            if (sessionSummaries.containsKey(user)) {
                UserSummary existingSummary = sessionSummaries.get(user);

                Long currentLength = existingSummary.getTotalSessionLength();
                Long newLength = currentLength + session.calculateSessionLength();
                existingSummary.setTotalSessionLength(newLength);

                int currentSessionCount = existingSummary.getSessionCount();
                int newSessionCount = currentSessionCount + 1;
                existingSummary.setSessionCount(newSessionCount);
            } else {
                Long length = session.calculateSessionLength();
                UserSummary newUserSummary = new UserSummary(user, 1, length);
                sessionSummaries.put(user, newUserSummary);
            }

        }

        for (Map.Entry<String, UserSummary> entry : sessionSummaries.entrySet()) {
            UserSummary userSummary = entry.getValue();
            System.out.println(userSummary.getUser() + " " + userSummary.getSessionCount() + " " + userSummary.getTotalSessionLength());
        }
    }

    private String findEarliestTime(List<String> lines) {
        // Just return the time from the first line in the list
        String line = lines.get(0);
        String[] elements = getElements(line);
        String time = elements[0];
        return time;
    }

    private String findFinishedTime(List<String> lines) {
        // Just return the time from the last line in the list
        String line = lines.get(lines.size() - 1);
        String[] elements = getElements(line);
        String time = elements[0];
        return time;
    }

    private String[] getElements(String line) {
        // every line has space in between the elements -this is to get rid of space and to be able to access each element
        String[] elements = line.trim().split(" ");
        return elements;
    }


    private String findNextEndPosition(List<String> lines, String user, int startingLineNumber, List<Integer> processedLines, String finishedTime) {
        // Starting reading at the next line and then read all lines one by one
        for (int i = startingLineNumber + 1; i < lines.size(); i++) {

            if (processedLines.contains(i)) {
                // This line has already been processed so ignored it
                continue;
            }

            String line = lines.get(i);

            if(!line.matches(LOGFORMAT)) {
                // Ignore all lines that do not match the expected log line format
                continue;
            }

            // Get the elements of interest
            String[] elements = getElements(line);
            String time = elements[0];
            String nextUser = elements[1];
            String position = elements[2];

            // If the current line has the same user and is of End position then this is the line we're interested in
            if (nextUser.equals(user) && position.equals("End")) {
                // Add line number to list so that we do not process it in future
                processedLines.add(i);
                // return time -represents end time
                return time;
            }
        }

        // If above loop did not find correct match then we have to return the total finished time of log file
        return finishedTime;
    }

}
