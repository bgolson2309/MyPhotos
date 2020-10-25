package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.wakeword.main.Constants;
import com.wakeword.util.PhotoManager;

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
    		boolean isValid = PhotoManager.validateToken(googleToken);
    		String albums = PhotoManager.listAlbums(googleToken);
    		System.out.println("ALBUM LIST = " + albums);
    		String AnAlbum = PhotoManager.listAlbumMedia(googleToken, "AMEXHWpANbSolnXXxx5o9BWI7vGh8miF-c_27A6Z_mM6IXNPP6B_Of7d6N7ZjvKv4jP657jtEWoj");
    		System.out.println("UTAH ALBUM MEDIA = " + AnAlbum);

    	}
    		
//		String awsConsentToken = input.getRequestEnvelope().getContext().getSystem().getApiAccessToken();

        return input.getResponseBuilder()
                .withSpeech(Constants.FIRST_VISIT)
                .withSimpleCard(Constants.MY_PHOTOS, Constants.FIRST_VISIT)
                .withShouldEndSession(false)
                .build();


	}
    

}