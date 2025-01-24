package com.qa.tests;
 
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
 
import com.qa.base.BaseTest;
import com.qa.constants.APIHttpStatus;
import com.qa.rest.client.RestClient;
import com.qa.utils.StringUtils;
 
import io.restassured.http.ContentType;
import io.restassured.response.Response;
 
public class PetStoreTest extends BaseTest {

	@BeforeMethod
	public void PetStoreTest() {
		restUtil = new RestClient();
	}
 
	
	@Test(priority=1,enabled=true)
	public void getDetailsForPetId()
	{
		restUtil.setUrlForMethod(baseURI+PETSTORE_ENDPOINT+"/10");
		restUtil.setContentType(ContentType.JSON);
		Response response = restUtil.sendRequestAndGetResponse().then().log().all()
				.assertThat()
				.statusCode(APIHttpStatus.OK_200.getCode()).extract().response();
		System.out.println(response.asPrettyString());
	}
	@Test(priority=2)
	public void addNewPet()
	{
		StringUtils st  = new StringUtils();
		Object content = st.readFileContentAsString("C:\\Users\\utkarsh-dubey\\eclipse-workspace\\Rest_Assured\\Pet.json");
		restUtil.setContentType(ContentType.JSON);
		restUtil.setBody(content);
		restUtil.setUrlForMethod(baseURI+PETSTORE_ENDPOINT);
		restUtil.setLog(true);
		String id = restUtil.postMessageAndGetResponse().then().log().all()
		.assertThat().statusCode(APIHttpStatus.OK_200.getCode()).extract().path("id");
		System.out.println("id of dog is :"+id);
	}
}