package com.qa.tests;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qa.base.BaseTest;
import com.qa.constants.APIHttpStatus;
import com.qa.rest.client.RestClient;
import com.qa.utils.LoggerService;
import com.qa.utils.StringUtils;

import io.restassured.http.ContentType;

public class ObjectUserTest extends BaseTest {
	
	Logger logger = LoggerService.getLogger(ObjectUserTest.class.getName());
	
	@BeforeMethod
	public void getUserSetup() {
		restUtil = new RestClient(prop, baseURI);
		restut = new RestClient(prop,baseURI);
	}
	
	
	@Test
	public void createObjectUserTest()
	{
		StringUtils st  = new StringUtils();
		Object content = st.readFileContentAsString("C:\\Users\\UTKARSH\\eclipse-workspace\\Rest_Assured\\file.json");
		restUtil.setContentType(ContentType.JSON);
		restUtil.setBody(content);
		restUtil.setUrlForMethod(baseURI);
		restUtil.setLog(true);
		String id = restUtil.postMessageAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.OK_200.getCode()).extract().path("id");
		
		RestClient restut = new RestClient(prop,baseURI);
		restut.setContentType(ContentType.JSON);
		restut.setUrlForMethod(baseURI);
		restut.setLog(true);
		restut.setParamkey("id");
		restut.setParamValue(id);
		restut.sendRequestAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.OK_200.getCode());
		
	}
	
	@Test
	public void updateObjectUserTest()
	{
		StringUtils st  = new StringUtils();
		Object content = st.readFileContentAsString("C:\\Users\\UTKARSH\\eclipse-workspace\\Rest_Assured\\file_update.json");
		restUtil.setContentType(ContentType.JSON);
		restUtil.setBody(content);
		restUtil.setUrlForMethod(baseURI+"/"+"ff8081818f04ae07018f0fafe0d61cd7");
		restUtil.setLog(true);
		String name = restUtil.updateMessageAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.OK_200.getCode()).extract().path("name");
		System.out.println(name);
	}
	
	@Test(enabled=false)
	public void deleteObjectUserTest()
	{
		restUtil.setContentType(ContentType.JSON);
		restUtil.setUrlForMethod("https://api.restful-api.dev/objects/ff8081818f04ae07018f0e83693d18bd");
		restUtil.setLog(true);
		restUtil.deleteAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.OK_200.getCode());
	}
}

