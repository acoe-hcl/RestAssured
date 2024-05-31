package com.qa.rest.client;


import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;


import com.qa.utils.StringUtils;

import com.qa.utils.*;

import com.qa.utils.JsonUtils;
import com.qa.frameworkexception.APIFrameworkException;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class RestClient {
	Logger logger = LoggerService.getLogger(RestClient.class.getName());
	private String jPathExpression;
	private Object body;
	private String urlForMethod;
	private ContentType contentType;
	private boolean log=false;
	private String postwithBody="True";



	public String getPostwithBody() {
		return postwithBody;
	}

	public void setPostwithBody(String postwithBody) {
		this.postwithBody = postwithBody;
	}

	public ContentType getContentType() {
		return contentType;
	}

	public void setContentType(ContentType contentType) {
		this.contentType = contentType;
	}

	public String getUrlForMethod() {
		return urlForMethod;
	}

	public void setUrlForMethod(String urlForMethod) {
		this.urlForMethod = urlForMethod;
	}

	public boolean isLog() {
		return log;
	}

	public void setLog(boolean log) {
		this.log = log;
	}
	public Object getBody() {
		return body;
	}

	public void setBody(Object body) {
		this.body = body;
	}

	public String getjPathExpression() {
		return jPathExpression;
	}

	public void setjPathExpression(String jPathExpression) {
		this.jPathExpression = jPathExpression;
	}
	
	@SuppressWarnings("unused")
	private Properties prop;
	@SuppressWarnings("unused")
	private String baseURI;

	public RestClient(Properties prop, String baseURI) {
		this.prop = prop;
		this.baseURI = baseURI;
	}
	Response response;

	private String paramkey;
	private String paramValue;
	private String headerKey;
	private String responseCodeForValidation ="200";
	private String retryForValueInResponse;
	private String formDataParamters;
	
	
	public String getFormDataParamters() {
		return formDataParamters;
	}

	public void setFormDataParamters(String formDataParamters) {
		this.formDataParamters = formDataParamters;
	}

	public String getRetryForValueInResponse() {
		return retryForValueInResponse;
	}

	public void setRetryForValueInResponse(String retryForValueInResponse) {
		this.retryForValueInResponse = retryForValueInResponse;
	}

	public String getResponseCodeForValidation() {
		return responseCodeForValidation;
	}

	public void setResponseCodeForValidation(String responseCodeForValidation) {
		this.responseCodeForValidation = responseCodeForValidation;
	}

	public String getTimesToCheck() {
		return timesToCheck;
	}

	public void setTimesToCheck(String timesToCheck) {
		this.timesToCheck = timesToCheck;
	}

	public String getWithTimeIntervalInSeconds() {
		return withTimeIntervalInSeconds;
	}

	public void setWithTimeIntervalInSeconds(String withTimeIntervalInSeconds) {
		this.withTimeIntervalInSeconds = withTimeIntervalInSeconds;
	}

	public String getEnableHttpRequestRetry() {
		return enableHttpRequestRetry;
	}

	public void setEnableHttpRequestRetry(String enableHttpRequestRetry) {
		this.enableHttpRequestRetry = enableHttpRequestRetry;
	}

	public String getMaxRetriesForHttpCall() {
		return maxRetriesForHttpCall;
	}

	public void setMaxRetriesForHttpCall(String maxRetriesForHttpCall) {
		this.maxRetriesForHttpCall = maxRetriesForHttpCall;
	}

	public String getTimeIntervalBetweenRetries() {
		return timeIntervalBetweenRetries;
	}

	public void setTimeIntervalBetweenRetries(String timeIntervalBetweenRetries) {
		this.timeIntervalBetweenRetries = timeIntervalBetweenRetries;
	}
	private String timesToCheck = "1";
	// In seconds
	private String withTimeIntervalInSeconds = "10";

	private String enableHttpRequestRetry = "1";
	private String maxRetriesForHttpCall = "1";
	private String timeIntervalBetweenRetries = "1";
	
	
	public String getHeaderKey() {
		return headerKey;
	}

	public void setHeaderKey(String headerKey) {
		this.headerKey = headerKey;
	}

	public String getHeaderValue() {
		return headerValue;
	}

	public void setHeaderValue(String headerValue) {
		this.headerValue = headerValue;
	}

	private String headerValue;

	public String getParamValue() {
		return paramValue;
	}

	public void setParamValue(String paramValue) {
		this.paramValue = paramValue;
	}

	public String getParamkey() {
		return paramkey;
	}

	public void setParamkey(String paramkey) {
		this.paramkey = paramkey;
	}
	
	/**
	 * Gets status code from the response already retrieved
	 *
	 * @return status code from response
	 */
	public int getStatusCodeFromResponse() {
		if (response != null) {
			return response.getStatusCode();
		} else {
			new APIFrameworkException("Null API Response. No status code can be retrieved.");
		}
		return 0;
	}
	
	/**
	 * To set rest assured config for handling timeout
	 *
	 * @return rest assured config
	 */
	
	private RequestSpecification getRequestSpecification() {
			RestAssured.config=RestAssuredConfig.config()
                    .httpClient(HttpClientConfig.httpClientConfig()
                            .setParam("http.socket.timeout",10000)
                            .setParam("http.connection.timeout", 10000));
		if(getParams().entrySet().size()>0)
		{
			return RestAssured.given().config(RestAssured.config).contentType(getContentType()).queryParams(getParams()).headers(getMapOfHeaders());
		}
		else {
		return RestAssured.given().config(RestAssured.config).contentType(getContentType()).headers(getMapOfHeaders());
		}
	}
	
	/**
	 * Forms map of headers based on inputs set
	 *
	 * Multiple headers can be set using headerkey and headervalue delimited by
	 * comma
	 *
	 * @return map of headers
	 */
	public Map<String, String> getMapOfHeaders() {
		int headerCount = 0;
		Map<String, String> headers = new HashMap<String, String>();
		if (!StringUtils.isNullOrEmpty(getHeaderKey()) && !StringUtils.isNullOrEmpty(getHeaderValue())) {
			for (String headerKey : getHeaderKey().split("\\,")) {
				headers.put(headerKey, getHeaderValue().split("\\,")[headerCount++]);
			}
		} else {
			logger.warn("No Headers set!!. No Header Key or Value passed.");
		}
		return headers;
	}

	/**
	 * Method to convert the string of parameters into Map of Name Value Pair for
	 * postUrlEncodedFormEntityAndGetResponseFile
	 *
	 * @param formDataParameters
	 *
	 * @return Map<NameValuePair>
	 */
	public Map<String, String> getMapOfNameValuePairBasedOnParameterString(String formDataParameters) {
		Map<String, String> mapOfFormDataParameters = new HashMap<String, String>();
		try {
			for (String parameter : formDataParameters.split(",")) {
				mapOfFormDataParameters.put(parameter.split("==")[0], parameter.split("==")[1]);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error(
					"FormDataParameters are not passed in the expected format. Please send the parameters in the format: Param1Key==Param1Value,Param2Key==Param2Value");
			new APIFrameworkException(
					"FormDataParameters are not sent in expected format. Please check execution logs for more details");
		}
		return mapOfFormDataParameters;
	}

	/**
	 * Forms map of query params based on inputs set
	 *
	 * Multiple query params can be set using param key and param value delimited by
	 * comma
	 *
	 * @return map of query params
	 */
	public Map<String, String> getParams() {
		int paramCount = 0;
		Map<String, String> params = new HashMap<String, String>();
		if (!StringUtils.isNullOrEmpty(getParamkey()) && !StringUtils.isNullOrEmpty(getParamValue())) {
			for (String paramKeys : getParamkey().split("\\,")) {
				params.put(paramKeys, getParamValue().split("\\,")[paramCount++]);
			}
		} else {
			logger.warn("No formParams set!!. No formParam Key or Value passed.");
		}
		return params;
	}
	
	/**
	 * Method send message and get response via POST 
	 * 
	 * @return response
	 */
	public Response postMessageAndGetResponse() {
		logger.info("Request URL for post message as json body is : " + getUrlForMethod());
		RequestSpecification requestSpecification = getRequestSpecification();
		try {
            //response =  getRequestSpecification().contentType(ContentType.JSON).body(body).log().all().when().post(urlToPostMessage);
            response = waitAndGetExpectedResponse(requestSpecification,"POST",getBody(),isLog(),getUrlForMethod(),getRetryForValueInResponse(),getTimesToCheck(),getWithTimeIntervalInSeconds());
			logger.info("Response received post sending the message: "+response.asString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while sending message via POST method");
			new APIFrameworkException(e.getMessage());
		}
		return response;
	
	}
	
	/**
	 * Method to send update message and get response via PUT
	 * 
	 * @return response
	 */
	public Response updateMessageAndGetResponse() {
		logger.info("Request URL for updating message as json body is : " + getUrlForMethod());
		RequestSpecification requestSpecification = getRequestSpecification();
		try {
            //response =  getRequestSpecification().contentType(ContentType.JSON).body(body).log().all().when().post(urlToPostMessage);
            response = waitAndGetExpectedResponse(requestSpecification,"PUT",getBody(),isLog(),getUrlForMethod(),getRetryForValueInResponse(),getTimesToCheck(),getWithTimeIntervalInSeconds());
			logger.info(response.asString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while updating message as json file");
			new APIFrameworkException(e.getMessage());
		}
		return response;
	}
	
	/**
	 * Method to remove object data via DELETE
	 * 
	 * @return response
	 */
	public Response deleteAndGetResponse() {
		logger.info("Request URL for delete message  is : " +getUrlForMethod());
		RequestSpecification requestSpecification = getRequestSpecification();
		try {
            //response =  getRequestSpecification().contentType(ContentType.JSON).body(body).log().all().when().post(urlToPostMessage);
            response = waitAndGetExpectedResponse(requestSpecification,"DELETE",getBody(),isLog(),getUrlForMethod(),getRetryForValueInResponse(),getTimesToCheck(),getWithTimeIntervalInSeconds());
			logger.info(response.asString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while updating deleting message");
			new APIFrameworkException(e.getMessage());
		}
		return response;
	}
	
	/**
	 * Method to send and get response via GET
	 * 
	 * @return response
	 */
	public Response sendRequestAndGetResponse() {
		logger.info("Request url for send request and Get Response is : "+getUrlForMethod());
		try {
			RequestSpecification requestSpecification = getRequestSpecification();
				//response = requestSpecification.contentType(ContentType.JSON).log().all().when().get(urlToGetMessage);
				response = waitAndGetExpectedResponse(requestSpecification,"GET",getBody(),isLog(),getUrlForMethod(),getRetryForValueInResponse(),getTimesToCheck(),getWithTimeIntervalInSeconds());
			logger.info(response.asString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while sending message via GET method");
			new APIFrameworkException(e.getMessage());
		}
		return response;
	}
	
	/**
	 * Waits and gets expected response based on retryForValueInResponse and polling
	 * logic.
	 *
	 * @param requestSpecification
	 * @param requestUrl
	 * @param retryForValueInResponse
	 * @param timesToCheck
	 * @param requestUrl
	 * @param message
	 * @param withTimeIntervalInSeconds
	 * @param responseFileToBeGenerated
	 * @return expected response in string
	 */
	public Response waitAndGetExpectedResponse(RequestSpecification requestSpecification, String methodType, Object messageBody, boolean log,
            String requestUrl, String retryForValueInResponse, String timesToCheck, String withTimeIntervalInSeconds) {
		int numberOfTimesToCheck = Integer.parseInt(timesToCheck);
		int calculatedTimeInterval = 0;
		String resultFromResponse = null;
		String responseInString = null;
		//requestSpecification = getRequestSpecification();
		if (StringUtils.isNullOrEmpty(requestUrl))
			throw new IllegalArgumentException("Request URL cannot be null or empty!!");
        if(methodType.equalsIgnoreCase("GET")){
        	if(log){
                response = requestSpecification.log().all().when().get(requestUrl);
        	}else {
        		response = requestSpecification.when().get(requestUrl);}
        }else if(methodType.equalsIgnoreCase("POST")){
        	if(getPostwithBody().equals("True"))
        	{
        	if (StringUtils.isNull(messageBody))
    			throw new IllegalArgumentException("Body cannot be null or empty!!");
        	if(log){
                response = requestSpecification.body(messageBody).log().all().when().post(requestUrl);
            }else {
        		response = requestSpecification.body(messageBody).when().post(requestUrl);}
        	}
        	else {
        		if(log){
                    response = requestSpecification.log().all().when().post(requestUrl);
                }else {
            		response = requestSpecification.when().post(requestUrl);}
            	}
        	}else if(methodType.equalsIgnoreCase("PUT")){
            	if (StringUtils.isNull(messageBody))
        			throw new IllegalArgumentException("Body cannot be null or empty!!");
            	if(log){
                    response = requestSpecification.body(messageBody).log().all().when().put(requestUrl);
                }else {
            		response = requestSpecification.body(messageBody).when().put(requestUrl);}
		} else if(methodType.equalsIgnoreCase("DELETE")){
			if(log){
				response = requestSpecification.log().all().when().delete(requestUrl);
			}else {
				response = requestSpecification.when().delete(requestUrl);}
		}
		response = retryAndGetResponseIfFailed(requestSpecification,methodType,log,messageBody,requestUrl,getStatusCodeFromResponse());
		//responseInString = response.asPrettyString();
		//validateResponse();
		TimeCalculator.start();
		try {
			if (!StringUtils.isNullOrEmpty(retryForValueInResponse)) {
				for (int iteratorForPolling = 1; iteratorForPolling <= numberOfTimesToCheck; iteratorForPolling++) {
					calculatedTimeInterval = new TimeIntervalHelper().getTimeInterval(iteratorForPolling,
							numberOfTimesToCheck, Integer.parseInt(withTimeIntervalInSeconds));

					resultFromResponse = getResultFromResponseBasedOnExpression(responseInString);
					if (StringUtils.isNullOrEmpty(resultFromResponse)
							|| !resultFromResponse.matches(retryForValueInResponse)) {
						logger.info("Expected value: " + retryForValueInResponse
								+ " not returned in the response. Waiting for " + calculatedTimeInterval
								+ " seconds......");
						Thread.sleep(calculatedTimeInterval * 1000);
						if (iteratorForPolling == numberOfTimesToCheck) {// End of polling
							logger.info("Expected value not found in the Response: " + responseInString);
							//generateResponseFileBasedOnFileType(responseInString, responseFileToBeGenerated);
							logger.debug(responseInString);
							throw new RuntimeException("Expected value: " + retryForValueInResponse
									+ " not returned in the response. Total Wait time: ~ "
									+ TimeCalculator.endAndGetElapsedTimeInSeconds() + " seconds");
						}
						if(methodType.equalsIgnoreCase("GET")){
				        	if(log){
				                response = requestSpecification.log().all().when().get(requestUrl);
				        	}else {
				        		response = requestSpecification.when().get(requestUrl);}
				        }else if(methodType.equalsIgnoreCase("POST")){
				        	if(getPostwithBody().equals("True"))
				        	{
				        	if (StringUtils.isNull(messageBody))
				    			throw new IllegalArgumentException("Body cannot be null or empty!!");
				        	if(log){
				                response = requestSpecification.body(messageBody).log().all().when().post(requestUrl);
				            }else {
				        		response = requestSpecification.body(messageBody).when().post(requestUrl);}
				        	}
				        	else {
				        		if(log){
				                    response = requestSpecification.log().all().when().post(requestUrl);
				                }else {
				            		response = requestSpecification.when().post(requestUrl);}
				            	}
						}else if(methodType.equalsIgnoreCase("PUT")){
			            	if (StringUtils.isNull(messageBody))
			        			throw new IllegalArgumentException("Body cannot be null or empty!!");
			            	if(log){
			                    response = requestSpecification.body(messageBody).log().all().when().put(requestUrl);
			                }else {
			            		response = requestSpecification.body(messageBody).when().put(requestUrl);}
						}else if(methodType.equalsIgnoreCase("DELETE")){
							if(log){
								response = requestSpecification.log().all().when().delete(requestUrl);
							}else {
								response = requestSpecification.when().delete(requestUrl);}
						}
						response = retryAndGetResponseIfFailed(requestSpecification,methodType,log,messageBody,requestUrl,getStatusCodeFromResponse());
						//responseInString = response.asPrettyString();
						//validateResponse();
					} else {
						logger.info("Expected result:" + retryForValueInResponse + " retrieved. Total wait time: ~ "
								+ TimeCalculator.endAndGetElapsedTimeInSeconds() + " seconds");
						break;
					}
				}
			}
		} catch (InterruptedException e) {
			logger.error("InterruptedException found!!");
			e.printStackTrace();
			new RuntimeException("InterruptedException found!! Check logs for more details");
		} catch (Exception e) {
			e.printStackTrace();
			new RuntimeException(
					"Exception found. !! Check logs for more details. Error Message: " + e.getMessage());
		}
		log=false;
		return response;
	}
	
	/**
	 * Method which retries send request and get response until the status code
	 * returned is not of the series expected response code for number of times and within time-interval
	 * configured for retries
	 *
	 * @param requestSpecification
	 * @param methodType
	 * @param requestUrl
	 * @param message
	 * @param statusCode
	 * @return response with or without retrying based on status code
	 */
	private Response retryAndGetResponseIfFailed(RequestSpecification requestSpecification, String methodType, boolean log,
												Object messageBody, String requestUrl, int statusCode) {
			if (requestSpecification == null) {
				throw new IllegalArgumentException("Request Specification cannot be null");
			}
			if (Boolean.parseBoolean(getEnableHttpRequestRetry())) {
				if (!Integer.toString(statusCode)
						.matches(getResponseCodeForValidation())) {
					logger.info(String.format(
							"Status Code: %s found in response. Retry sending request and get response starts.",
							statusCode));
					for (int iteratorForRetry = 1; iteratorForRetry <= Integer
							.parseInt(getMaxRetriesForHttpCall()); iteratorForRetry++) {
						logger.info("Http call retry: " + (iteratorForRetry));
						if(methodType.equalsIgnoreCase("GET")){
				        	if(log){
				                response = requestSpecification.log().all().when().get(requestUrl);
				        	}else {
				        		response = requestSpecification.when().get(requestUrl);}
				        }else if(methodType.equalsIgnoreCase("POST")){
				        	if(getPostwithBody().equals("True"))
				        	{
				        	if (StringUtils.isNull(messageBody))
				    			throw new IllegalArgumentException("Body cannot be null or empty!!");
				        	if(log){
				                response = requestSpecification.body(messageBody).log().all().when().post(requestUrl);
				            }else {
				        		response = requestSpecification.body(messageBody).when().post(requestUrl);}
				        	}
				        	else {
				        		if(log){
				                    response = requestSpecification.log().all().when().post(requestUrl);
				                }else {
				            		response = requestSpecification.when().post(requestUrl);}
				            	}
						} else if(methodType.equalsIgnoreCase("PUT")){
			            	if (StringUtils.isNull(messageBody))
			        			throw new IllegalArgumentException("Body cannot be null or empty!!");
			            	if(log){
			                    response = requestSpecification.body(messageBody).log().all().when().put(requestUrl);
			                }else {
			            		response = requestSpecification.body(messageBody).when().put(requestUrl);}
						}else if(methodType.equalsIgnoreCase("DELETE")){
							if(log){
								response = requestSpecification.log().all().when().delete(requestUrl);
							}else {
								response = requestSpecification.when().delete(requestUrl);}
						} else {
							logger.error("Supported http calls, GET, POST and DELETE!!");
							new APIFrameworkException("Supported http calls: GET, POST and DELETE!!");
						}
						if (Integer.toString(response.getStatusCode())
								.matches(getResponseCodeForValidation())) {
							logger.info(String.format("Success response received after %s retries", iteratorForRetry));
							break;
						}
						try {
							logger.info(String.format("Waiting for: %s seconds",getTimeIntervalBetweenRetries()));
							Thread.sleep(Integer.parseInt(getTimeIntervalBetweenRetries()) * 1000);
						} catch (InterruptedException e) {
							throw new RuntimeException(e);
						}
					}
				}
			}
		return response;
	}
	
	/**
	 * Method to get result from response based on Expression
	 *
	 * @param responseInString
	 * @return value from xml/json file
	 */
	public String getResultFromResponseBasedOnExpression(String responseInString) {
		JsonUtils jsonUtilsObject;
		String resultFromResponse;
		if (!StringUtils.isNullOrEmpty(getjPathExpression())) {
			jsonUtilsObject = new JsonUtils();
			jsonUtilsObject.setOptional("true");
			resultFromResponse = jsonUtilsObject.getJsonResultFromJpathExpression(responseInString,
					getjPathExpression());
		}
		else {
			throw new RuntimeException(
					"Please set XpathXpression or JPathExpression based on the response type.");
		}
		return resultFromResponse;
	}

	/**
	 * Method to post message as url encoded form with parameters
	 *
	 * @param urlToPostMessage
	 * @param responseFileToBeGenerated
	 * @param formDataParameters
	 * @return String: Generated response file name
	 */
	public Response postUrlEncodedFormEntityAndGetResponseFile() {
		logger.info("Request URL for post message as json body is : " + getUrlForMethod());
		try {
//            response = getRequestSpecification().formParams(getMapOfNameValuePairBasedOnParameterString(getFormDataParamters())).
//            		log().all().when().post(getUrlForMethod());
			response = waitAndGetExpectedResponse(getRequestSpecification().formParams(getMapOfNameValuePairBasedOnParameterString(getFormDataParamters())),"POST",getBody(),isLog(),getUrlForMethod(),getRetryForValueInResponse(),getTimesToCheck(),getWithTimeIntervalInSeconds());
			logger.info("Response received post sending the message: "+response.asString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error while sending message via POST method");
			new APIFrameworkException(e.getMessage());
		}
		return response;
	}
	
}
