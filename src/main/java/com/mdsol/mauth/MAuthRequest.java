//
// Copyright (c) 2018 Medidata Solutions, Inc. All rights reserved.
//

package com.mdsol.mauth;

import com.mdsol.mauth.util.MAuthHeadersHelper;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * Wrapper for an incoming MAuth request data necessary to authenticate it.
 */
public class MAuthRequest {

	public static final String MAUTH_TIME_HEADER_NAME = "x-mws-time";
	public static final String MAUTH_AUTHENTICATION_HEADER_NAME = "x-mws-authentication";

	private static final String VALIDATION_EXCEPTION_MESSAGE_TEMPLATE = "%s cannot be null or empty.";

	private final UUID appUUID;
	private final String requestSignature;
	private final byte[] messagePayload;
	private final InputStream inputStream;
	private final String httpMethod;
	private final long requestTime;
	private final String resourcePath;

	public MAuthRequest(String authenticationHeaderValue, byte[] messagePayload, String httpMethod, String timeHeaderValue, String resourcePath) {
		validateNotBlank(authenticationHeaderValue, "Authentication header value");
		UUID appUUID = MAuthHeadersHelper.getAppUUIDFromAuthenticationHeader(authenticationHeaderValue);
		String requestSignature = MAuthHeadersHelper.getSignatureFromAuthenticationHeader(authenticationHeaderValue);

		validateNotBlank(timeHeaderValue, "Time header value");
		long requestTime = MAuthHeadersHelper.getRequestTimeFromTimeHeader(timeHeaderValue);

		validateNotBlank(httpMethod, "Http method");
		validateNotBlank(resourcePath, "Resource path");
		validateRequestTime(requestTime);
		if (messagePayload == null) {
			messagePayload = new byte[] {};
		}

		this.appUUID = appUUID;
		this.requestSignature = requestSignature;
		this.messagePayload = messagePayload;
		this.inputStream = null;
		this.httpMethod = httpMethod;
		this.requestTime = requestTime;
		this.resourcePath = resourcePath;
	}

	public MAuthRequest(String authenticationHeaderValue, InputStream inputStream, String httpMethod, String timeHeaderValue, String resourcePath) {
		validateNotBlank(authenticationHeaderValue, "Authentication header value");
		UUID appUUID = MAuthHeadersHelper.getAppUUIDFromAuthenticationHeader(authenticationHeaderValue);
		String requestSignature = MAuthHeadersHelper.getSignatureFromAuthenticationHeader(authenticationHeaderValue);

		validateNotBlank(timeHeaderValue, "Time header value");
		long requestTime = MAuthHeadersHelper.getRequestTimeFromTimeHeader(timeHeaderValue);

		validateNotBlank(httpMethod, "Http method");
		validateNotBlank(resourcePath, "Resource path");
		validateRequestTime(requestTime);
		
		this.appUUID = appUUID;
		this.requestSignature = requestSignature;
		this.messagePayload = null;
		this.inputStream = inputStream;
		this.httpMethod = httpMethod;
		this.requestTime = requestTime;
		this.resourcePath = resourcePath;
	}

	public UUID getAppUUID() {
		return appUUID;
	}

	public String getRequestSignature() {
		return requestSignature;
	}

	public byte[] getMessagePayload() {
		return messagePayload == null ? messagePayload : Arrays.copyOf(messagePayload, messagePayload.length);
	}
	
	public InputStream getInputStream() {
		return inputStream;
	}

	public String getHttpMethod() {
		return httpMethod;
	}

	public long getRequestTime() {
		return requestTime;
	}

	public String getResourcePath() {
		return resourcePath;
	}

	private void validateNotBlank(String field, String fieldNameInExceptionMessage) {
		if (StringUtils.isBlank(field)) {
			throw new IllegalArgumentException(
					String.format(VALIDATION_EXCEPTION_MESSAGE_TEMPLATE, fieldNameInExceptionMessage));
		}
	}

	private void validateRequestTime(long requestTime) {
		if (requestTime <= 0) {
			throw new IllegalArgumentException("Request time cannot be negative or 0.");
		}
	}

	public static final class Builder {

		private String authenticationHeaderValue;
		private byte[] messagePayload;
		private InputStream inputStream;
		private String httpMethod;
		private String timeHeaderValue;
		private String resourcePath;

		public static Builder get() {
			return new Builder();
		}

		public Builder withAuthenticationHeaderValue(String authenticationHeaderValue) {
			this.authenticationHeaderValue = authenticationHeaderValue;
			return this;
		}

		public Builder withTimeHeaderValue(String timeHeaderValue) {
			this.timeHeaderValue = timeHeaderValue;
			return this;
		}

		public Builder withMessagePayload(byte[] messagePayload) {
			this.messagePayload = messagePayload;
			return this;
		}
		
		public Builder withInputStream(InputStream inputStream) {
			this.inputStream = inputStream;
			return this;
		}

		public Builder withHttpMethod(String httpMethod) {
			this.httpMethod = httpMethod;
			return this;
		}

		public Builder withResourcePath(String resourcePath) {
			this.resourcePath = resourcePath;
			return this;
		}

		public MAuthRequest build() {
			return inputStream == null
					? new MAuthRequest(authenticationHeaderValue, messagePayload, httpMethod, timeHeaderValue, resourcePath)
					: new MAuthRequest(authenticationHeaderValue, inputStream, httpMethod, timeHeaderValue, resourcePath);
		}
	}
}
