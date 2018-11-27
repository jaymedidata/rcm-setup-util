//
// Copyright (c) 2018 Medidata Solutions, Inc. All rights reserved.
//

package com.mdsol.mauth.utils;

import java.security.PublicKey;
import java.util.UUID;

public interface ClientPublicKeyProvider {

	/**
	 * Returns the associated public key for a given application UUID.
	 *
	 * @param appUUID, UUID of the application for which we want to retrieve its
	 *        public key.
	 * @return {@link PublicKey} registered in MAuth for the application with given
	 *         appUUID.
	 */
	PublicKey getPublicKey(UUID appUUID);
}
