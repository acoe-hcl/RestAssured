package com.qa.utils;

import org.apache.log4j.Logger;

import com.qa.frameworkexception.APIFrameworkException;

public class TimeIntervalHelper {

	static Logger logger = LoggerService.getLogger(TimeIntervalHelper.class.getName());
	//final String pollWaitLogicProperty = "true";
	final String pollWaitLogicFlag = "false";
	final int maxWaitTime = 100;

	/**
	 * Method to get time interval based on current iteration, total number of times
	 * to check and time interval
	 * 
	 * @param counter
	 * @param totalNumberOfTimes
	 * @param timeInterval
	 * @return
	 */
	public Integer getTimeInterval(int counter, int totalNumberOfTimes, int timeInterval) {
		int calculatedTimeIntervalInSeconds = 0;
		if (Boolean.parseBoolean(pollWaitLogicFlag)) {
			logger.debug("Using incremental poll wait intervals");
			calculatedTimeIntervalInSeconds = counter * timeInterval;
			if (totalWaitTime(counter - 1, timeInterval) >= maxWaitTime) {
				logger.info(String.format("Total Elapsed time: %s seconds",
						TimeCalculator.endAndGetElapsedTimeInSeconds()));
				new APIFrameworkException(
						String.format("Expected result not found!! Max wait time %s seconds exceeded.", maxWaitTime));
			} else {
				logger.debug("Max wait time not reached. Total wait time till now: "
						+ TimeCalculator.endAndGetElapsedTimeInSeconds());
			}
		} else if (!Boolean.parseBoolean(pollWaitLogicFlag)) {
			logger.debug("Using non incremental poll wait intervals");
			calculatedTimeIntervalInSeconds = timeInterval;
		} else {
			logger.error(String.format("Define property %s in your Framework.properties", pollWaitLogicFlag));
			new APIFrameworkException(
					String.format("Define property %s in your Framework.properties", pollWaitLogicFlag));
		}
		logger.debug(String.format("Total calculated time interval for next wait: %s seconds",
				calculatedTimeIntervalInSeconds));
		return calculatedTimeIntervalInSeconds;
	}

	/**
	 * Method to calculate total wait time for the poll loop based on time interval
	 * and current iteration
	 * 
	 * @param counter
	 * @param timeInterval
	 * @return total wait time
	 */
	public int totalWaitTime(int counter, int timeInterval) {
		int totalWaitTime = 0;
		for (int iteratorForTotalTimeCalculation = counter; iteratorForTotalTimeCalculation >= 1; iteratorForTotalTimeCalculation--) {
			totalWaitTime = totalWaitTime + iteratorForTotalTimeCalculation * timeInterval;
		}
		return totalWaitTime;
	}
}
