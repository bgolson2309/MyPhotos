package com.wakeword.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;

public class PhotoManager {
	
    /*
     * Make a call to see if we have the scope we need by testing the access token for the scope we need
     * 
     * https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=ya29.a0AfH6SMBnTBJpSYMQTO9h_LlRZgffxC_jnypezYrJeEoZ8c-koXqxTdD_5zd4w7SzhiBT7rrugLQ9x4VO_6AObrw2d4wvoLRIiYhBQulvOkeBqiF0xqSbhIwSs_ifrRNeatFum-2swdu5XFMxR8hIoA1SCHX3y5icUqFggZdpfWc
     * 
     * {
     * "issued_to": "774522006372-lck9vv9lmtnsi2p64c9oocl6rligfrhu.apps.googleusercontent.com",
     * "audience": "774522006372-lck9vv9lmtnsi2p64c9oocl6rligfrhu.apps.googleusercontent.com",
     * "scope": "https://www.googleapis.com/auth/photoslibrary.readonly",
     * "expires_in": 3224,
     *  "access_type": "offline"  
     * }
     */
    public static boolean validateToken(String token)
    {
    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
    	      .uri(URI.create("https://www.googleapis.com/oauth2/v1/tokeninfo?access_token=" + token))
    	      .POST(BodyPublishers.noBody())
    	      .build();
    	
    	HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
			return false;
		}

        return response.body().contains("https://www.googleapis.com/auth/photoslibrary.readonly");
    }

    public static String listAlbums(String token)
    {
    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
    	      .uri(URI.create("https://photoslibrary.googleapis.com/v1/albums"))
    	      .setHeader("Authorization", " Bearer " + token)
    	      .GET()
    	      .build();
    	  
    	HttpResponse<String> response = null;
		try {
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

        return response.toString();
    }
    
}
