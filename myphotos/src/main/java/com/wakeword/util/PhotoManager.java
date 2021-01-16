package com.wakeword.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    /*
     * Will be {} if no albums
     * https://developers.google.com/photos/library/reference/rest/v1/albums/list
     */
    public static String listAlbums(String token)
    {
    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
    	      .uri(URI.create("https://photoslibrary.googleapis.com/v1/albums?pageSize=50"))
    	      .setHeader("Authorization", " Bearer " + token)
    	      .GET()
    	      .build();
    	  
    	HttpResponse<String> response = null;
		try {
			
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			System.out.println(e.getStackTrace());
		}

        return response.body();
    }
    
    /*
     * https://lh3.googleusercontent.com/lr/AFBm1_YNc3EyLwMkzEbjO7EP-KSNXmqthRAdRSQuee2cKxSUnBK6ruWxsQ3zNbE2Yv1FO7o9aKAde24aHEZhuq3njm1vFzuM99rEfRnU25GgNlxERLkquINbSekJkftv-Q6ZR9yz9Yeb7ipdwAqm8Ra71QQcTGkzbj5rrLJ7zbdkH04w7h1kcf9NwPZtYcTJ_9BfiyjNJz_6n0W5dySDriqMTkvs4hKkHFmLC8sV5P2wQw3IeL84sFF8I81Hza9SxqYmIyMyq9LL9iRu_3lPJa6aNNONcnw1I6b-PVkydVABekl2htCNtnMfgclsy6Ltem1cgckW4KadGUKS9gqvQtJ5vZpT7V_ZyecG0ddjnd648DRkDin7eJL4ATqpgaqrjCZPqLtmMNki99xrx5xAJnVhiUlZ1HG-ZjHSjQyDYXOmq_OKBiYEmHFqacF-9OcHr75fejL8w7ST-zhDmgr_nTtWGpWMAMovt2pG-MEZGiTLFIh8DcsJW1v_-hvaEqI_tuo_cUkOLI7CaWr_65mbuAHURGKmPllZhUSPxkP2SBvAVxXGemXy9YqT5PKXWbxQYHzoK1LqcwRD_BjGK6GXNOrDWB21hFwdKtddBC883MqWeeTlIJJAexiUOQ3kTaRhpXN_l_IB5_wtZEAMWMVgjs5BI1JrAaPD-6qYu4fTOj83HXLCC_-KbAkmrz_4qrqU-dCCNPsHIH990o9meuFQy-uQwKANuPGrY5GYRA300a-ea-4yk5kKjRHr5U6x4_zSlfY6TktnJVeNW2puZJGJsjnC5uc=w2048-h1024
     * Use media base URL to display image per the UI size (https://developer.amazon.com/en-US/docs/alexa/alexa-presentation-language/apl-viewport-property.html)
     * https://developers.google.com/photos/library/guides/access-media-items 
     */
    public static String listAlbumMedia(String token, String albumID)
    {
    	
        HashMap<String, String> values = new HashMap<String, String>();
        values.put("albumId",albumID);
        ObjectMapper objectMapper = new ObjectMapper();
        String requestBody = null;
		try {
			requestBody = objectMapper.writeValueAsString(values);
		} catch (JsonProcessingException e1) {
			e1.printStackTrace();
		}
    	
    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
    	      .uri(URI.create("https://photoslibrary.googleapis.com/v1/mediaItems:search?pageSize=100"))
    	      .setHeader("Authorization", " Bearer " + token)
    	      .POST(HttpRequest.BodyPublishers.ofString(requestBody))
    	      .build();
    	  
    	HttpResponse<String> response = null;
		try {
			
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

        return response.body();
    }
    
    /*
     * https://lh3.googleusercontent.com/lr/AFBm1_YNc3EyLwMkzEbjO7EP-KSNXmqthRAdRSQuee2cKxSUnBK6ruWxsQ3zNbE2Yv1FO7o9aKAde24aHEZhuq3njm1vFzuM99rEfRnU25GgNlxERLkquINbSekJkftv-Q6ZR9yz9Yeb7ipdwAqm8Ra71QQcTGkzbj5rrLJ7zbdkH04w7h1kcf9NwPZtYcTJ_9BfiyjNJz_6n0W5dySDriqMTkvs4hKkHFmLC8sV5P2wQw3IeL84sFF8I81Hza9SxqYmIyMyq9LL9iRu_3lPJa6aNNONcnw1I6b-PVkydVABekl2htCNtnMfgclsy6Ltem1cgckW4KadGUKS9gqvQtJ5vZpT7V_ZyecG0ddjnd648DRkDin7eJL4ATqpgaqrjCZPqLtmMNki99xrx5xAJnVhiUlZ1HG-ZjHSjQyDYXOmq_OKBiYEmHFqacF-9OcHr75fejL8w7ST-zhDmgr_nTtWGpWMAMovt2pG-MEZGiTLFIh8DcsJW1v_-hvaEqI_tuo_cUkOLI7CaWr_65mbuAHURGKmPllZhUSPxkP2SBvAVxXGemXy9YqT5PKXWbxQYHzoK1LqcwRD_BjGK6GXNOrDWB21hFwdKtddBC883MqWeeTlIJJAexiUOQ3kTaRhpXN_l_IB5_wtZEAMWMVgjs5BI1JrAaPD-6qYu4fTOj83HXLCC_-KbAkmrz_4qrqU-dCCNPsHIH990o9meuFQy-uQwKANuPGrY5GYRA300a-ea-4yk5kKjRHr5U6x4_zSlfY6TktnJVeNW2puZJGJsjnC5uc=w2048-h1024
     * Use media base URL to display image per the UI size (https://developer.amazon.com/en-US/docs/alexa/alexa-presentation-language/apl-viewport-property.html)
     * https://developers.google.com/photos/library/guides/access-media-items 
     */
    public static String listMedia(String token)
    {
    	
    	
	   	 String filter =  "{" + 
	   	        "  \"pageSize\": \"100\"," + 
	   	 		"  \"filters\": {" + 
	   	 		"    \"mediaTypeFilter\": {" + 
	   	 		"      \"mediaTypes\": [" + 
	   	 		"        \"PHOTO\"" + 
	   	 		"      ]" + 
	   	 		"    }" + 
	   	 		"  }" + 
	   	 		"}";
	      
   	
    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
    	      .uri(URI.create("https://photoslibrary.googleapis.com/v1/mediaItems:search"))
    	      .setHeader("Authorization", " Bearer " + token)
    	      .POST(HttpRequest.BodyPublishers.ofString(filter))
    	      .build();
    	  
    	HttpResponse<String> response = null;
		try {
			
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

        return response.body();
    }
    
    /*
     * https://lh3.googleusercontent.com/lr/AFBm1_YNc3EyLwMkzEbjO7EP-KSNXmqthRAdRSQuee2cKxSUnBK6ruWxsQ3zNbE2Yv1FO7o9aKAde24aHEZhuq3njm1vFzuM99rEfRnU25GgNlxERLkquINbSekJkftv-Q6ZR9yz9Yeb7ipdwAqm8Ra71QQcTGkzbj5rrLJ7zbdkH04w7h1kcf9NwPZtYcTJ_9BfiyjNJz_6n0W5dySDriqMTkvs4hKkHFmLC8sV5P2wQw3IeL84sFF8I81Hza9SxqYmIyMyq9LL9iRu_3lPJa6aNNONcnw1I6b-PVkydVABekl2htCNtnMfgclsy6Ltem1cgckW4KadGUKS9gqvQtJ5vZpT7V_ZyecG0ddjnd648DRkDin7eJL4ATqpgaqrjCZPqLtmMNki99xrx5xAJnVhiUlZ1HG-ZjHSjQyDYXOmq_OKBiYEmHFqacF-9OcHr75fejL8w7ST-zhDmgr_nTtWGpWMAMovt2pG-MEZGiTLFIh8DcsJW1v_-hvaEqI_tuo_cUkOLI7CaWr_65mbuAHURGKmPllZhUSPxkP2SBvAVxXGemXy9YqT5PKXWbxQYHzoK1LqcwRD_BjGK6GXNOrDWB21hFwdKtddBC883MqWeeTlIJJAexiUOQ3kTaRhpXN_l_IB5_wtZEAMWMVgjs5BI1JrAaPD-6qYu4fTOj83HXLCC_-KbAkmrz_4qrqU-dCCNPsHIH990o9meuFQy-uQwKANuPGrY5GYRA300a-ea-4yk5kKjRHr5U6x4_zSlfY6TktnJVeNW2puZJGJsjnC5uc=w2048-h1024
     * Use media base URL to display image per the UI size (https://developer.amazon.com/en-US/docs/alexa/alexa-presentation-language/apl-viewport-property.html)
     * https://developers.google.com/photos/library/guides/access-media-items 
     */
    public static String searchMediaByCategories(String token, String categories[])
    {
    	
    	 String cat =  "{" + 
    	 		"  \"pageToken\": \"\"," + 
    	 		"  \"pageSize\": 100," + 
    	 		"  \"filters\": {" + 
    	 		"    \"contentFilter\": {" + 
    	 		"      \"includedContentCategories\": [";
    	 
         for (int i=0; i < categories.length; i++) {
        	 cat = cat + categories[i];
        	 if (i < (categories.length -1)) {
            	 cat = cat + ",";
        	 }
         }
         cat = cat + "      ]" + 
         		"    }" + 
         		"  }" + 
         		"}";
         
       System.out.println(cat);
    	HttpClient client = HttpClient.newHttpClient();
    	HttpRequest request = HttpRequest.newBuilder()
    	      .uri(URI.create("https://photoslibrary.googleapis.com/v1/mediaItems:search?pageSize=100"))
    	      .setHeader("Authorization", " Bearer " + token)
    	      .POST(HttpRequest.BodyPublishers.ofString(cat))
    	      .build();
    	  
    	HttpResponse<String> response = null;
		try {
			
			response = client.send(request, HttpResponse.BodyHandlers.ofString());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

        return response.body();
    }
    
}
