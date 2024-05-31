package com.qa.tests;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.qa.base.BaseTest;
import com.qa.constants.APIHttpStatus;
import com.qa.rest.client.RestClient;

import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class AmadeusAPITest extends BaseTest{

	public String accessToken;
	
	@BeforeMethod
	public void flightAPiSetup() {
		restUtil = new RestClient(prop, baseURI);
		getAccessToken();
	}
	
	public void getAccessToken()
	{
		restUtil.setPostwithBody("false");
		restUtil.setFormDataParamters("grant_type==client_credentials,client_id==TAnRnsU5lASXZ8mPGdwRQZMoQzhu6Gwv,"
				+ "client_secret==VjjgfcJilNAzcSJw");
		restUtil.setUrlForMethod(baseURI+AMADEUS_TOKEN_ENDPOINT);
		restUtil.setContentType(ContentType.URLENC);
		accessToken= restUtil.postUrlEncodedFormEntityAndGetResponseFile().then().log().all()
		.assertThat()
		.statusCode(APIHttpStatus.OK_200.getCode())
		.extract().path("access_token");
	}
	
	@Test
	public void getFlightInfo()
	{   restUtil = new RestClient(prop, baseURI);
		restUtil.setHeaderKey("Authorization");
		restUtil.setHeaderValue("Bearer "+accessToken);
		restUtil.setContentType(ContentType.JSON);
		restUtil.setParamkey("origin,maxPrice");
		restUtil.setParamValue("PAR,200");
		restUtil.setLog(true);
		restUtil.setUrlForMethod(baseURI+AMADEUS_FLIGHTBOKKING_ENDPOINT);
		Response response = restUtil.sendRequestAndGetResponse().then().log().all()
		.assertThat()
		.statusCode(APIHttpStatus.OK_200.getCode()).extract().response();
		JsonPath js = response.jsonPath();
		String type = js.get("data[0].type");
		System.out.println(type);
	}
}
