package com.mdsol.rcm.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.mdsol.mauth.SignerConfiguration;
import com.mdsol.mauth.apache.HttpClientRequestSigner;
import com.mdsol.mauth.apache.SignerHttpRequestInterceptor;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class SetupService {

	private static final String SEPARATER = System.getProperty("line.separator");

	public CloseableHttpClient getHttpClient(String mauthPath) {
		Config config = ConfigFactory.parseFile(new File(mauthPath));
		SignerConfiguration configuration = new SignerConfiguration(config);

		final HttpClientRequestSigner httpClientRequestSigner = new HttpClientRequestSigner(configuration);
		final SignerHttpRequestInterceptor signerHttpRequestInterceptor = new SignerHttpRequestInterceptor(
				httpClientRequestSigner);

		CloseableHttpClient httpClient = HttpClients.custom().addInterceptorFirst(signerHttpRequestInterceptor).build();
		return httpClient;
	}

	public void sendApiRequest(String mauthPath, String apiURL) {
		CloseableHttpClient httpClient = getHttpClient(mauthPath);
		HttpGet request = new HttpGet(apiURL);
		try {
			System.out.println("Request: " + apiURL);
			HttpResponse response = httpClient.execute(request);
			HttpEntity responseEntity = response.getEntity();
			String content = EntityUtils.toString(responseEntity);
			System.out.println("Response: " + content);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void subToArchon(Properties prop, String subNamePrefix, String mauthPath) {
		System.out.println("subNamePrefix is:" + subNamePrefix);

		String studiesTopic = prop.getProperty(SetupConfig.ARCHON_SUB_URL_STUDIES);
		String sitesTopic = prop.getProperty(SetupConfig.ARCHON_SUB_URL_STUDY_ENVIRONMENT_SITES);
		String countriesTopic = prop.getProperty(SetupConfig.ARCHON_SUB_URL_STUDY_ENVIRONMENT_COUNTRIES);
		String configurationTypesTopic = prop.getProperty(SetupConfig.ARCHON_SUB_URL_CONFIGIRATION_TYPES);
		String configurationTypeRolesTopic = prop.getProperty(SetupConfig.ARCHON_SUB_URL_CONFIGIRATION_TYPE_ROLES);
		String studyEnvironmentAssignmentsTopic = prop
				.getProperty(SetupConfig.ARCHON_SUB_URL_STUDY_ENVIRONMENT_ASSIGNMENTS);
		String clientDivisionAssignmentsTopic = prop
				.getProperty(SetupConfig.ARCHON_SUB_URL_CLIENT_DIVISION_SCHEME_ASSIGNMENTS);

		System.out.println("studiesTopic is:" + studiesTopic);

		StringBuilder endpoints = new StringBuilder();
		endpoints.append(getMessageEndPoint(studiesTopic, subNamePrefix.concat("_studies"),
				prop.getProperty(SetupConfig.PROP_KEY_STUDIES), mauthPath)).append(SEPARATER);
		endpoints.append(getMessageEndPoint(sitesTopic, subNamePrefix.concat("_sites"),
				prop.getProperty(SetupConfig.PROP_KEY_STUDY_ENVIRONMENT_SITES), mauthPath)).append(SEPARATER);
		;
		endpoints.append(getMessageEndPoint(countriesTopic, subNamePrefix.concat("_countries"),
				prop.getProperty(SetupConfig.PROP_KEY_STUDY_ENVIRONMENT_COUNTRIES), mauthPath)).append(SEPARATER);
		;
		endpoints.append(getMessageEndPoint(configurationTypesTopic, subNamePrefix.concat("_configuration_types"),
				prop.getProperty(SetupConfig.PROP_KEY_CONFIGIRATION_TYPES), mauthPath)).append(SEPARATER);
		;
		endpoints.append(
				getMessageEndPoint(configurationTypeRolesTopic, subNamePrefix.concat("_configuration_type_roles"),
						prop.getProperty(SetupConfig.PROP_KEY_CONFIGIRATION_TYPE_ROLES), mauthPath))
				.append(SEPARATER);
		;
		endpoints.append(getMessageEndPoint(studyEnvironmentAssignmentsTopic,
				subNamePrefix.concat("_study_environment_assignments"),
				prop.getProperty(SetupConfig.PROP_KEY_STUDY_ENVIRONMENT_ASSIGNMENTS), mauthPath)).append(SEPARATER);
		;
		endpoints.append(
				getMessageEndPoint(clientDivisionAssignmentsTopic, subNamePrefix.concat("_client_division_assignments"),
						prop.getProperty(SetupConfig.PROP_KEY_CLIENT_DIVISION_SCHEME_ASSIGNMENTS), mauthPath));
		writeToFile(endpoints.toString());
	}

	private void writeToFile(String content) {
		try (PrintWriter out = new PrintWriter("SQS_ARNs")) {
			out.println(content);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String getMessageEndPoint(String subUrl, String subName, String key, String mauthPath) {
		CloseableHttpClient httpClient = getHttpClient(mauthPath);
		HttpPost request = new HttpPost(subUrl);
		String archonResponse = null;

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("subscription_type", "sqs"));
		params.add(new BasicNameValuePair("name", subName));

		try {
			request.setEntity(new UrlEncodedFormEntity(params));
			System.out.println("Request: " + request);
			HttpResponse response = httpClient.execute(request);
			HttpEntity responseEntity = response.getEntity();
			archonResponse = EntityUtils.toString(responseEntity);
			System.out.println("Response : " + archonResponse);

			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(archonResponse);
			String endpointUrl = json.get("message_endpoint").toString();
			String queueArn = endpointUrl.split("/")[4];
			return key.concat("=").concat(queueArn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return archonResponse;
	}

	public void generateBuildingBlocks(Properties props, String mauthPath, String appCtId) {
		// Get app configuration type uuid
		Config config = ConfigFactory.parseFile(new File(mauthPath));
		String appUuid = config.getString("app.uuid");
		
		// Get props
		String permissionsApiUrl = props.getProperty(SetupConfig.DALTON_API_HOST) + "/v1/permissions";
		String configurationTypeRolesApiUrl = props.getProperty(SetupConfig.DALTON_API_HOST) + "/v1/configuration_types/" + appCtId+ "/roles";
		
		System.out.println("appUuid is:" + appUuid);
		System.out.println("appCtId is:" + appCtId);
		System.out.println("permissionsApiUrl is:" + permissionsApiUrl);
		System.out.println("configurationTypeRolesApiUrl is:" + configurationTypeRolesApiUrl);
		
		// Create Permissions
		JSONParser parser = new JSONParser();
		Object obj;
		try {
			String permissions = FileUtils.getFileContent("json/permissions/permissions.json");
			System.out.println("permissions:" + permissions);

			obj = parser.parse(permissions);
			JSONArray jsonArray = (JSONArray) obj;
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject payload = (JSONObject) jsonArray.get(i);
				String payloadJsonString = payload.toJSONString();
				System.out.println("Payload: " + payloadJsonString);
				post(permissionsApiUrl, payloadJsonString, mauthPath);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Create Building Blocks
		Arrays.stream(BuildingBlocks.values()).forEach(bb -> {
			String filePath = "json/building_blocks/" + bb.getBbName() + ".json";
			String bbPayload = FileUtils.getFileContent(filePath);
			bbPayload = bbPayload.replace("{_APP_UUID_}", appUuid).replace("{_CONFIGURATION_TYPE_UUID_}", appCtId);
			System.out.println("Building Block:" + bbPayload);
			post(configurationTypeRolesApiUrl, bbPayload, mauthPath);
		});
	}
	
	public void registerAppWithCheckmate(Properties properties, String mauthPath, String appUrl) {
		Config config = ConfigFactory.parseFile(new File(mauthPath));
		String appUuid = config.getString("app.uuid");
		
		String urlApp = properties.getProperty(SetupConfig.CHECKMATE_API_HOST)  + "/v1/checkmated_apps";
		String urlAction = properties.getProperty(SetupConfig.CHECKMATE_API_HOST)  + "/v1/checkmated_apps/" + appUuid + "/actions";
		
		String appRegistrationJson = FileUtils.getFileContent("json/checkmate/app_registration.json");
		appRegistrationJson = appRegistrationJson.replace("{_APP_UUID_}", appUuid).replace("{_APP_URL_}", appUrl);
		post(urlApp, appRegistrationJson, mauthPath);
		
		String actionClientDivisionsJson = FileUtils.getFileContent("json/checkmate/action_client_divisions.json");
		actionClientDivisionsJson = actionClientDivisionsJson.replace("{_APP_UUID_}", appUuid);
		post(urlAction, actionClientDivisionsJson, mauthPath);
		
		String actionStudiesJson = FileUtils.getFileContent("json/checkmate/action_studies.json");
		actionStudiesJson = actionStudiesJson.replace("{_APP_UUID_}", appUuid);
		post(urlAction, actionStudiesJson, mauthPath);
		
		String actionStudyEnvironmentsJson = FileUtils.getFileContent("json/checkmate/action_study_environments.json");
		actionStudyEnvironmentsJson = actionStudyEnvironmentsJson.replace("{_APP_UUID_}", appUuid);
		post(urlAction, actionStudyEnvironmentsJson, mauthPath);
		
		String actionStudyEnvironmentSitesJson = FileUtils.getFileContent("json/checkmate/action_study_environment_sites.json");
		actionStudyEnvironmentSitesJson = actionStudyEnvironmentSitesJson.replace("{_APP_UUID_}", appUuid);
		post(urlAction, actionStudyEnvironmentSitesJson, mauthPath);
	}
	
	private String post(String apiURL, String payload, String mauthPath) {
		CloseableHttpClient httpClient = getHttpClient(mauthPath);
		String apiResponse = null;
		HttpPost request = new HttpPost(apiURL);
		StringEntity entity = new StringEntity(payload, ContentType.APPLICATION_JSON);
		request.setEntity(entity);

		try {
			System.out.println("Request: " + apiURL);
			HttpResponse response = httpClient.execute(request);
			HttpEntity responseEntity = response.getEntity();
			apiResponse = EntityUtils.toString(responseEntity);
			System.out.println("Response: " + apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}

	public String getFileContent(String filePath) {
		try {
			return new String(Files.readAllBytes(Paths.get(filePath)));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
