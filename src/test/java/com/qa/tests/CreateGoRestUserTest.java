package com.qa.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.qa.base.BaseTest;
import com.qa.rest.client.*;
import com.qa.constants.APIConstants;
import com.qa.constants.APIHttpStatus;
import com.qa.pojo.User;
import com.qa.utils.ExcelUtil;
import com.qa.utils.LoggerService;
import com.qa.utils.StringUtils;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.response.ValidatableResponseOptions;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.equalTo;

import java.io.File;

import org.apache.log4j.Logger;


public class CreateGoRestUserTest extends BaseTest{
	
	Logger logger = LoggerService.getLogger(CreateGoRestUserTest.class.getName());
	
	@BeforeMethod
	public void getUserSetup() {
		restUtil = new RestClient(prop, baseURI);
		restut = new RestClient(prop,baseURI);
	}
	
	
	@DataProvider
	public Object[][] getUserTestData() {
		return new Object[][] {
			{"Subodh", "male", "active"},
			{"Seema", "female", "inactive"},
			{"Madhuri", "female", "active"}
		};
	}
	
	@DataProvider
	public Object[][] getUserTestSheetData() {
		return ExcelUtil.getTestData(APIConstants.GOREST_USER_SHEET_NAME);
	}
	
	

	@Test(dataProvider = "getUserTestData")
	public void createUserTest(String name, String gender, String status) {
		
		User user = new User(name, StringUtils.getRandomEmailId(), gender, status);	
		restUtil.setHeaderKey("Authorization");
		//String token = "Bearer "+prop.getProperty("tokenId");
		restUtil.setHeaderValue( "Bearer "+prop.getProperty("tokenId"));
		restUtil.setBody(user);
		restUtil.setContentType(ContentType.JSON);
		restUtil.setUrlForMethod(baseURI+GOREST_ENDPOINT);
		restUtil.setLog(true);
		Integer userId = restUtil.postMessageAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.CREATED_201.getCode())
			.extract().path("id");
		
		restut.setHeaderKey("Authorization");
		restut.setHeaderValue( "Bearer "+prop.getProperty("tokenId"));
		restut.setContentType(ContentType.JSON);
		restut.setUrlForMethod(baseURI+GOREST_ENDPOINT+"/"+userId);
		restut.setLog(true);
		restut.sendRequestAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.OK_200.getCode())
		.assertThat().body("id", equalTo(userId));
	}
	
}
	