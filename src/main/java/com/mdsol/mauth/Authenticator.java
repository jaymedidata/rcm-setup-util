//
// Copyright (c) 2018 Medidata Solutions, Inc. All rights reserved.
//

package com.mdsol.mauth;

public interface Authenticator {

	/**
	 * Performs the validation of an incoming HTTP request.
	 * <p/>
	 * The validation process consists of recreating the mAuth hashed signature from
	 * the request data and comparing it to the decrypted hash signature from the
	 * mAuth header.
	 *
	 * @param mAuthRequest Data from the incoming HTTP request necessary to perform
	 *                     the validation.
	 * @return True or false indicating if the request is valid or not with respect
	 *         to mAuth.
	 */
	boolean authenticate(MAuthRequest mAuthRequest);

}
