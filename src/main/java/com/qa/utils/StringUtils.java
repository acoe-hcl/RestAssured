package com.qa.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.apache.log4j.Logger;



public class StringUtils {

	Logger logger = LoggerService.getLogger(StringUtils.class.getName());
	private static final String DEFAULT_FILE_CONTENT_DELIMTER = "\\Z";
	public static String getRandomEmailId() {
		return "api" + System.currentTimeMillis() + "@api.com";
	}

	/**
	 * Method to check is the string null or empty
	 * 
	 * @param inputStr
	 * @return true/false
	 */
	public static boolean isNullOrEmpty(String inputStr) {
		if (inputStr == null || inputStr.isEmpty())
		{
			return true;
		}

		return false;
	}
	
	public static boolean isNull(Object message)
	{
		if(message==null)
		{
			return true;
		
		}
		return false;
	}
	
	public String readFileContentAsString(String fileName, String delimiter) {
//        if (Boolean.parseBoolean(ConfigureExecution.getSkipStep())) {
//            logger.warn("Skip Step Enabled. Test Step Execution Skipped");
//            return "Skip Step Enabled. Test Step Execution Skipped";
//        }
        String fileContent = null;
        Scanner scanner = null;
        FileInputStream fileInputStream = null;
        try {
            if (StringUtils.isNullOrEmpty(fileName) || StringUtils.isNullOrEmpty(delimiter))
                throw new IllegalArgumentException("File Name or delimiter cannot be null or empty!!");

            logger.debug(String.format("Reading file content %s as string data.", fileName));
            logger.debug("Delimiter set to read file content as string: " + delimiter);

            fileInputStream = new FileInputStream(getFile(fileName));
            scanner = new Scanner(fileInputStream,"UTF-8");
            fileContent = scanner.useDelimiter(delimiter).next();
            scanner.close();
            fileInputStream.close();
        } catch (FileNotFoundException _ex) {
            logger.error(String.format(
                    "Error when reading file content and converting to string. Please check if the file %s is not empty and if delimiter %s is correct.",
                    fileName, delimiter), _ex);
            _ex.printStackTrace();
            new RuntimeException(_ex.getMessage());
        } catch (IllegalArgumentException _ex) {
            logger.error(String.format(
                    "IllegalArgumentException!! when reading file content and converting to string. Please check if the file %s is not empty and if delimiter %s is correct.",
                    fileName, delimiter), _ex);
            new RuntimeException(_ex.getMessage());
        } catch (Exception _ex) {
            logger.error(String.format(
                    "Exception!! when reading file content and converting to string. Please check if the file %s is not empty and if delimiter %s is correct.",
                    fileName, delimiter), _ex);
            _ex.printStackTrace();
            new RuntimeException(
                    String.format("IllegalArgumentException!! While reading file: %s as string.", fileName));
        }
        return fileContent;
    }
	
	public String readFileContentAsString(String fileName)
	{
	    return readFileContentAsString(fileName, DEFAULT_FILE_CONTENT_DELIMTER);
	    
	}
	
	  /**
     * Helper method to get file object from file name/path
     *
     * @param fileName
     * @return fileobject
     */
    public static File getFile(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName))
            throw new RuntimeException(String.format("File name: %s cannot be null or empty.", fileName));

        try {
            File file = new File(fileName);
            return file;
        } catch (Exception _ex) {
            throw new RuntimeException(String.format("Could not get handle on File %s.", fileName), _ex);
        }
    }
}
