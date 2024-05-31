package com.qa.utils;

import java.io.File;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class LoggerService {
	public String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	static Logger logger;

	public static Logger getLogger() {
		logger = Logger.getLogger(LoggerService.class.getCanonicalName());
		return logger;
	}

	public static Logger getLogger(String className) {
		logger = Logger.getLogger(className);
		return logger;
	}

	static {
		String log4jConfPath = System.getProperty("user.dir") + File.separator + "log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
	}

	/**
	 * WIKI used method to add info logs in the logger
	 */
	public void info() {
		LoggerService.getLogger().info(getMessage());
	}

	/**
	 * WIKI used method to add debug logs in the logger
	 */
	public void debug() {
		LoggerService.getLogger().debug(getMessage());
	}

	/**
	 * WIKI used method to add error logs in the logger
	 */
	public void error() {
		LoggerService.getLogger().error(getMessage());
	}

}
