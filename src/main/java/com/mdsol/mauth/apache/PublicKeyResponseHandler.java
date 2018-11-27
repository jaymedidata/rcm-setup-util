//
// Copyright (c) 2018 Medidata Solutions, Inc. All rights reserved.
//

package com.mdsol.mauth.apache;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdsol.mauth.exception.HttpClientPublicKeyProviderException;

public class PublicKeyResponseHandler implements ResponseHandler<String> {

	@Override
	public String handleResponse(HttpResponse response) throws IOException {
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity entity = response.getEntity();
			String responseAsString = EntityUtils.toString(entity, StandardCharsets.UTF_8);
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readTree(responseAsString).findValue("public_key_str").asText();
		} else {
			throw new HttpClientPublicKeyProviderException(
					"Invalid response code returned by server: " + response.getStatusLine().getStatusCode());
		}
	}
}
