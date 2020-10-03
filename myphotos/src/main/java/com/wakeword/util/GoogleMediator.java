package com.wakeword.util;

import java.util.Collections;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.apache.GoogleApacheHttpTransport;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;


public class GoogleMediator {
    private static Logger LOG = getLogger(GoogleMediator.class);
	private static final String CLIENT_ID = "774522006372-lck9vv9lmtnsi2p64c9oocl6rligfrhu.apps.googleusercontent.com";
	
	 
	public static boolean validatetoken(String token) 
	{

			try {
				GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
				    .setAudience(Collections.singletonList(CLIENT_ID))
				    .build();
	
				GoogleIdToken idToken;

//				idToken = verifier.verify(token);
//				if (idToken != null) {
//					  Payload payload = idToken.getPayload();
//
//					  // Print user identifier
//					  String userId = payload.getSubject();
//					  LOG.debug("GOOGLE User ID: " + userId);
//
//					  // Get profile information from payload
//					  LOG.debug(payload.getEmail());
//					  LOG.debug(payload.getEmail());
//					  LOG.debug((String) payload.get("name"));
//					  LOG.debug((String) payload.get("picture"));
//					  LOG.debug((String) payload.get("locale"));
//					  LOG.debug((String) payload.get("family_name"));
//					  LOG.debug((String) payload.get("given_name"));
//					  return true;
//				} else {
//					LOG.debug("Invalid Google ID token." + token);
//						return false;
//				}
			} catch (Exception e) {
				System.out.println(e.getStackTrace());
				LOG.debug(e.getStackTrace().toString());
			}		
					
			return false;
	}

}
