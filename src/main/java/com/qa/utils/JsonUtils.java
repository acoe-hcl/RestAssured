package com.qa.utils;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.qa.frameworkexception.APIFrameworkException;

import io.restassured.response.Response;


public class JsonUtils {

	Logger logger = LoggerService.getLogger(JsonUtils.class.getName());
	private String optional;
	public String getOptional() {
		return optional;
	}

	public void setOptional(String optional) {
		this.optional = optional;
	}

	/**
	 * This method return value from json string for the jpath expression passed
	 * 
	 * @param jsonResponse
	 * @param jPathExpression
	 * @return value from json
	 */
	public String getJsonResultFromJpathExpression(String jsonResponse, String jPathExpression) {
		Object jsonValueObject = null;
		String jsonValue = null;
		if (jPathExpression.matches("^COUNT\\[(.*)\\]")) {
			jsonValueObject = findNumberOfElementsMatchingJpath(jsonResponse, jPathExpression);
		} else {
			jsonValueObject = getJsonValueFromJpathExpression(jsonResponse, jPathExpression);
		}
		if (jsonValueObject != null)
			jsonValue = jsonValueObject.toString().replaceAll("^\\[", "").replaceAll("\\]$", "").replaceAll("\"", "");
		jsonValueObject = null;
		return jsonValue;
	}
	
	/**
	 * This method returns number of elements matching jpath expression in the json
	 * 
	 * @param jsonStringToFindElementsCount
	 * @param jpathExpressionToFindElementsCount
	 * @return matching count of elements found for jpath expression
	 */
	public int findNumberOfElementsMatchingJpath(String jsonStringToFindElementsCount,
			String jpathExpressionToFindElementsCount) {
		int numberOfElementsMatchingJpathExpression = 0;
		try {
			if (StringUtils.isNullOrEmpty(jsonStringToFindElementsCount)
					|| StringUtils.isNullOrEmpty(jpathExpressionToFindElementsCount))
				throw new IllegalArgumentException(
						"fileNameToFindElementsCount or jpathExpressionToFindElementsCount cannot be null or empty");
			Object objectFromJsonBasedOnJpathExpression = getJsonValueFromJpathExpression(jsonStringToFindElementsCount,
					jpathExpressionToFindElementsCount);
			numberOfElementsMatchingJpathExpression = ((net.minidev.json.JSONArray) objectFromJsonBasedOnJpathExpression)
					.size();

			// If matching element found
			if (numberOfElementsMatchingJpathExpression > 0)
				logger.info(String.format("Found : %s element/s matching jpath expression: %s",
						numberOfElementsMatchingJpathExpression, jpathExpressionToFindElementsCount));
			logger.debug(String.format("Found : %s element/s matching jpath expression: %s in json String: %s",
					numberOfElementsMatchingJpathExpression, jpathExpressionToFindElementsCount,
					jsonStringToFindElementsCount));

			logger.debug(String.format("Number of elements matching from json: %s for jpath expression: %s are: %d",
					jsonStringToFindElementsCount, jpathExpressionToFindElementsCount,
					numberOfElementsMatchingJpathExpression));
		} catch (IllegalArgumentException e) {
			logger.error(String.format(
					"Error while getting number of elements matching with jpath expression: %s in json String: %s",
					jpathExpressionToFindElementsCount, jsonStringToFindElementsCount));
			e.printStackTrace();
			new RuntimeException(e.getMessage());
		} catch (Exception e) {
			logger.error(String.format(
					"Error while getting number of elements matching with jpath expression: %s in json String: %s",
					jpathExpressionToFindElementsCount, jsonStringToFindElementsCount));
			e.printStackTrace();
			new RuntimeException(e.getMessage());
		}

		return numberOfElementsMatchingJpathExpression;
	}

	/**
	 * Method to get values from Json tag/field based on j-path expression
	 * 
	 * @param jsonResponse    - String value containing json response
	 * @param jPathExpression - To get value from json tag/field
	 * @return Array of values retrieved
	 */
	private Object getJsonValueFromJpathExpression(String jsonResponse, String jPathExpression) {
		DocumentContext jsonContext = null;
		Object dataObject = null;
		try {
			jPathExpression = extractJpathExpression(jPathExpression);
			if (StringUtils.isNullOrEmpty(jsonResponse) || StringUtils.isNullOrEmpty(jPathExpression))
				throw new IllegalArgumentException("Json Response or J path expression cannot be null of empty!!");
			jsonContext = JsonPath.parse(jsonResponse);
			dataObject = jsonContext.read(jPathExpression);
			logger.debug(String.format("Value from Json is: %s for Jpath Expression: %s", dataObject.toString(),
					jPathExpression));
			if (StringUtils.isNullOrEmpty(dataObject.toString())) {
				throw new RuntimeException(String.format("Value not found for JPath :%s in Json Response:\n",
						jPathExpression, jsonResponse));
			}
		} catch (PathNotFoundException pathNotFoundException) {
			if (!StringUtils.isNullOrEmpty(getOptional())) {
				if (getOptional().equalsIgnoreCase("true")) {
					logger.debug(
							"Path not found Ignored!!. Since Optional flag is enabled for JPath: " + jPathExpression);
					return null;
				} else {
					throw new RuntimeException(String
							.format("Path not found for JPath :%s in Json Response:\n", jPathExpression, jsonResponse));
				}
			} else {
				throw new RuntimeException(String.format("Path not found for JPath :%s in Json Response:\n",
						jPathExpression, jsonResponse));
			}
		} catch (IllegalArgumentException e) {
			logger.error(
					String.format("Error while getting value from json using jpathExpression:%s", jPathExpression));
			logger.debug(String.format("Error while getting value from json: %s \n using jpathExpression: %s",
					jsonResponse, jPathExpression));
			new RuntimeException("IllegalArgumentException!! " + e.getMessage());
		} catch (Exception e) {
			logger.error(
					String.format("Error while getting value from json using jpathExpression:%s", jPathExpression));
			logger.debug(String.format("Error while getting value from json: %s \n using jpathExpression: %s",
					jsonResponse, jPathExpression));
			e.printStackTrace();
			new RuntimeException("Exception!! " + e.getMessage());
		}
		return dataObject;
	}
	
	/**
	 * Method to get extract jpath when combined with supported methods
	 * 
	 * @param jPathExpression
	 * @return extracted jpath expression
	 */
	private String extractJpathExpression(String jPathExpression) {
		Pattern patternToCheckJpathExpression = Pattern.compile("^\\w+\\[(.*)\\]");
		Matcher matcher = patternToCheckJpathExpression.matcher(jPathExpression);
		while (matcher.find()) {
			return matcher.group(1);
		}
		return jPathExpression;
	}
	
	private String getJsonResponseAsString(Response response) {
        return response.getBody().asString();
	}
	
	
	public <T> T read(Response response, String jsonPath) {
		String jsonResponse =  getJsonResponseAsString(response);
        try {
        	return JsonPath.read(jsonResponse, jsonPath);
        }
        catch(PathNotFoundException e) {
        	e.printStackTrace();
        	throw new APIFrameworkException(jsonPath + "is not found...");
        }
	}
	
	public <T> List<T> readList(Response response, String jsonPath) {
		String jsonResponse =  getJsonResponseAsString(response);
        try {
        	return JsonPath.read(jsonResponse, jsonPath);
        }
        catch(PathNotFoundException e) {
        	e.printStackTrace();
        	throw new APIFrameworkException(jsonPath + "is not found...");
        }
	}
	
	
	public <T> List<Map<String, T>> readListOfMaps(Response response, String jsonPath) {
		String jsonResponse =  getJsonResponseAsString(response);
        try {
        	return JsonPath.read(jsonResponse, jsonPath);
        }
        catch(PathNotFoundException e) {
        	e.printStackTrace();
        	throw new APIFrameworkException(jsonPath + "is not found...");
        }
	}

}
