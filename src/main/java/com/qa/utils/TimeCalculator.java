package com.qa.utils;

import java.util.concurrent.TimeUnit;

public class TimeCalculator {

	private static long startTime = 0;
	private static long endTime = 0;

	public static void start() {
		startTime = System.nanoTime();
	}

	public static long endAndGetElapsedTimeInSeconds() {
		endTime = System.nanoTime();
		return TimeUnit.SECONDS.convert((endTime - startTime), TimeUnit.NANOSECONDS);
	}
}
