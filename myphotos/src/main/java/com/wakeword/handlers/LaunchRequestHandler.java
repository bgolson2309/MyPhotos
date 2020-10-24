package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.wakeword.dto.CustomerManager;
import com.wakeword.main.Constants;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;
public class LaunchRequestHandler implements RequestHandler  {
    private static Logger LOG = getLogger(LaunchRequestHandler.class);
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }
    public Optional<Response> handle(HandlerInput input) {

    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	LOG.debug("GOOGLE ACCESS-TOKEN = " + googleToken);
    	
    	if (googleToken == null)
    	{
            String speechText = "Please use the Alexa app to link your Google account "
                    + "with My Photos.";

            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .withLinkAccountCard()
                    .build();    	
        } else {
    		boolean isValid = validateToken(googleToken);
        	//make an APi call
   
    	}
    		
//		String awsConsentToken = input.getRequestEnvelope().getContext().getSystem().getApiAccessToken();
//    	String userId = input.getRequestEnvelope().getContext().getSystem().getUser().getUserId();
//	 	CustomerManager m = new CustomerManager(userId);
//	 	boolean customerExists = m.hasCustomer(userId);  // object in S3?

        return input.getResponseBuilder()
                .withSpeech(Constants.FIRST_VISIT)
                .withSimpleCard(Constants.MY_PHOTOS, Constants.FIRST_VISIT)
                .withShouldEndSession(false)
                .build();


	}
    
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
     *  "access_type": "online"  <-- needs to be offline!!!
     * }
     */
    private boolean validateToken(String token)
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

        System.out.println(response.body());
        return response.body().contains("https://www.googleapis.com/auth/photoslibrary.readonly");
    }
}