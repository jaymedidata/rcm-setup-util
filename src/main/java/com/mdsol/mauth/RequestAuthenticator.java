//
// Copyright (c) 2018 Medidata Solutions, Inc. All rights reserved.
//

package com.mdsol.mauth;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Security;
import java.util.Arrays;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import com.mdsol.mauth.exception.MAuthValidationException;
import com.mdsol.mauth.util.CurrentEpochTimeProvider;
import com.mdsol.mauth.util.EpochTimeProvider;
import com.mdsol.mauth.util.MAuthSignatureHelper;
import com.mdsol.mauth.utils.ClientPublicKeyProvider;

public class RequestAuthenticator implements Authenticator {

	private static final Logger logger = LoggerFactory.getLogger(RequestAuthenticator.class);

	private final ClientPublicKeyProvider clientPublicKeyProvider;
	private final long requestValidationTimeoutSeconds;
	private final EpochTimeProvider epochTimeProvider;

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	/**
	 * Uses 10L as default value for request validation timeout
	 * {@link com.mdsol.mauth.utils.ClientPublicKeyProvider} as the
	 * EpochTimeProvider
	 *
	 * @param clientPublicKeyProvider
	 */
	public RequestAuthenticator(ClientPublicKeyProvider clientPublicKeyProvider) {
		this(clientPublicKeyProvider, 10L);
	}

	/**
	 * Uses {@link com.mdsol.mauth.utils.ClientPublicKeyProvider} as the
	 * EpochTimeProvider
	 *
	 * @param clientPublicKeyProvider
	 * @param requestValidationTimeoutSeconds
	 */
	public RequestAuthenticator(ClientPublicKeyProvider clientPublicKeyProvider, long requestValidationTimeoutSeconds) {
		this(clientPublicKeyProvider, requestValidationTimeoutSeconds, new CurrentEpochTimeProvider());
	}

	public RequestAuthenticator(ClientPublicKeyProvider clientPublicKeyProvider, long requestValidationTimeoutSeconds,
			EpochTimeProvider epochTimeProvider) {
		this.clientPublicKeyProvider = clientPublicKeyProvider;
		this.requestValidationTimeoutSeconds = requestValidationTimeoutSeconds;
		this.epochTimeProvider = epochTimeProvider;
	}

	@Override
	public boolean authenticate(MAuthRequest mAuthRequest) {
		if (!(validateTime(mAuthRequest.getRequestTime()))) {
			final String message = "MAuth request validation failed because of timeout "
					+ requestValidationTimeoutSeconds + "s";
			logger.error(message);
			throw new MAuthValidationException(message);
		}

		PublicKey clientPublicKey = clientPublicKeyProvider.getPublicKey(mAuthRequest.getAppUUID());
		// Decrypt the signature with public key from requesting application.
		byte[] decryptedSignature = MAuthSignatureHelper.decryptSignature(clientPublicKey, mAuthRequest.getRequestSignature());
		decryptedSignature = Hex.decode(new String(decryptedSignature));
		// Recreate the plain text signature, based on the incoming request parameters,
		// and hash it.
		
		StringBuilder sb = new StringBuilder();
		sb.append(mAuthRequest.getHttpMethod());
		sb.append('\n');
		sb.append(mAuthRequest.getResourcePath());
		sb.append('\n');
		byte[] part1 = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
		InputStream part2 = mAuthRequest.getInputStream();
		sb = new StringBuilder();
		sb.append('\n');
		sb.append(mAuthRequest.getAppUUID().toString());
		sb.append('\n');
		sb.append(mAuthRequest.getRequestTime());
		byte[] part3 = sb.toString().getBytes(StandardCharsets.ISO_8859_1);
		SequenceInputStream stream = new SequenceInputStream(new ByteArrayInputStream(part1), new SequenceInputStream(part2, new ByteArrayInputStream(part3)));
		byte[] digestedString = MAuthSignatureHelper.getHexEncodedDigestedString(stream);
		return Arrays.equals(digestedString, decryptedSignature);
	}

	// Check epoch time is not older than specified interval.
	private boolean validateTime(long requestTime) {
		long currentTime = epochTimeProvider.inSeconds();
		return (currentTime - requestTime) < requestValidationTimeoutSeconds;
	}
}
