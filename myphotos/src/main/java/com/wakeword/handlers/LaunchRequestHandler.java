package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.response.ResponseBuilder;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import com.wakeword.dto.Album;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.amazon.ask.request.Predicates.requestType;


public class LaunchRequestHandler implements RequestHandler  {
    private static Logger LOG = getLogger(LaunchRequestHandler.class);
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }
    public Optional<Response> handle(HandlerInput input) {
        ResponseBuilder responseBuilder = input.getResponseBuilder();
    	Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String albumsString, speechText, albumsJson = null;
/*
 *     	try {
        	if (persistentAttributes.containsKey("PremiumAccess")) {
        		System.out.println("YES - WE HAVE LONG TERM ATTTRIBUTE PREMIUM ACCESS");
        	}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	boolean hasPremiumAccess = false;
    	persistentAttributes.put("PremiumAccess", Boolean.valueOf(hasPremiumAccess));
 */
    	if (googleToken == null || (!PhotoManager.validateToken(googleToken)))
    	{
            speechText = "Please use the Alexa application to link your Google account with My Photos.";
            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .withLinkAccountCard()
                    .build();    	
        } else {
        		ObjectMapper objectMapper = new ObjectMapper();      		
    			albumsString = PhotoManager.listAlbums(googleToken);
    			try {
            		Album[] albums = objectMapper.readValue(albumsString.substring(13), Album[].class); 
            		albumsJson = AplUtil.buildAlbumData(albums);
    	    	} catch (Exception e) {
    	    		System.out.println(e.getMessage());
    	    	}
    	    	speechText = "Welcome to My Photos.";
    	}
/*    	
    		// test saving album list on session and bought access on Persistent layer
    	input.getAttributesManager().getSessionAttributes().put("AlbumList", albumsString);
    	input.getAttributesManager().setPersistentAttributes(persistentAttributes);
    	input.getAttributesManager().savePersistentAttributes(); // Save long term attributes to Dynamo
    	    		// list most recent 100 images
       	String response = PhotoManager.listMedia(googleToken);
    	System.out.println("LIST MEDIA RESPONSE = " + response);
   			//test filter by category

    	String[] categories = new String[1];
    	categories[0] = "\"" + "PETS" + "\"";
    	String catResults = PhotoManager.searchMediaByCategories(googleToken, categories);
    	System.out.println("CAT = "+catResults);

 */	

    	
        if (AplUtil.supportsApl(input)) {
            try {
                // Retrieve the JSON document and put into a string/object map
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() {};
                Map<String, Object> document = mapper.readValue(new File("apl_album_list_template.json"), documentMapType);
                Map<String, Object> data = mapper.readValue(albumsJson, documentMapType);
                
                // Instructs the device to play the audio response defined in the specified document 
                RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
                        .withToken("AlbumListToken")
                        .withDocument(document)
                        .withDatasources(data)
                        .build();

                return input.getResponseBuilder()
                        .withSpeech(speechText)
                        .addDirective(renderDocumentDirective)
                        .build();

            } catch (IOException e) {
                throw new AskSdkException("Unable to read or deserialize the APL document", e);
            }
        } else {
            speechText = "My Photos is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
        }

        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("My Photos", speechText)
            .build();
	}

}