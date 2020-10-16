package com.wakeword.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import com.fasterxml.jackson.core.JsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.auth.Credentials;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.photos.library.v1.PhotosLibraryClient;
import com.google.photos.library.v1.PhotosLibrarySettings;



public class GoogleMediator {
    private static Logger LOG = getLogger(GoogleMediator.class);
	private static final String CLIENT_ID = "774522006372-lck9vv9lmtnsi2p64c9oocl6rligfrhu.apps.googleusercontent.com";
	
	public static void list(String token) 
	{
		GoogleCredential credentials = new GoogleCredential().setFromTokenResponse(token);

		FixedCredentialsProvider credentialsProvider = FixedCredentialsProvider.create(credential);
		try {
			PhotosLibrarySettings settings =
				     PhotosLibrarySettings.newBuilder()
				    .setCredentialsProvider(
				        FixedCredentialsProvider.create()) 
				    .build();
	    	  //listAlbums(client);
	        } catch (Exception e) {
	          // log
	        }

			
	}
	 
	public static void validatetoken(String token) 
	{
		LOG.debug("....in validate token and token = " + token);
			try {
		        JacksonFactory jacksonFactory = new JacksonFactory();

				GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), jacksonFactory)
				    .setAudience(Collections.singletonList(CLIENT_ID))
				    .setIssuer("accounts.google.com")
				    .build();
				
				LOG.debug("....in validate token BEFORE verify of " + token);		
				
				GoogleIdToken idToken = verifier.verify(token);
				
				LOG.debug("....in validatetoken AFTER verify ");

				if (idToken != null) {
					  LOG.debug("GOOGLE token is invalid");
				} else {
					LOG.debug("Google token is valid");
				}
			} catch (GeneralSecurityException se) {
				se.printStackTrace();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}

}
