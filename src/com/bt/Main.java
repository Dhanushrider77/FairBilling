package com.bt;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
	    String pathToFile = args[0];

	    SessionCalculator sessionCalculator = new SessionCalculator();
		try {
			sessionCalculator.printSessionData(pathToFile);
		} catch (IOException e) {
			System.err.print("Unable to process log file:" + e.getMessage());
		}
	}
}
