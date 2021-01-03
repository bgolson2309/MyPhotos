package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.request.RequestHelper;
import com.amazon.ask.response.ResponseBuilder;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import com.wakeword.dto.Album;
import com.wakeword.dto.MediaItem;

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
    	
   	
    	try {
        	if (persistentAttributes.containsKey("PremiumAccess")) {
        		System.out.println("YES - WE HAVE LONG TERM ATTTRIBUTE PREMIUM ACCESS");
        	}
    	} catch (Exception e) {
    		System.out.println(e.getMessage());
    	}
    	
    	boolean hasPremiumAccess = false;
    	persistentAttributes.put("PremiumAccess", Boolean.valueOf(hasPremiumAccess));
    	String albumsString = null;
    	String albumsJson = null;

    	
    	if (googleToken == null)
    	{
            String speechText = "Please use the Alexa app to link your Google account with My Photos.";

            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .withLinkAccountCard()
                    .build();    	
        } else {
    		if(PhotoManager.validateToken(googleToken)) {       		
        		ObjectMapper objectMapper = new ObjectMapper();      		
    			
    			albumsString = PhotoManager.listAlbums(googleToken);
    			try {
            		Album[] albums = objectMapper.readValue(albumsString.substring(13), Album[].class); 
            		albumsJson = AplUtil.buildAlbumData(albums);
            		
    	    	} catch (Exception e) {
    	    		System.out.println(e.getMessage());
    	    	}
    			System.out.println("ALBUM JSON = " + albumsJson);

    		} else {
    			// handle invalid google token case
    		}
    	}
    	
    	// test saving album list on session and bought access on Persistent layer
    	input.getAttributesManager().getSessionAttributes().put("AlbumList", albumsString);
    	input.getAttributesManager().setPersistentAttributes(persistentAttributes);
    	input.getAttributesManager().savePersistentAttributes(); // Save long term attributes to Dynamo
    	
    	
    	String speechText = "Welcome to My Photos.";
    	// Check for APL support on the user's device
        if (RequestHelper.forHandlerInput(input)
                .getSupportedInterfaces()
                .getAlexaPresentationAPL() != null) {

            try {
                // Retrieve the JSON document and put into a string/object map
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String, Object>> documentMapType = 
                    new TypeReference<HashMap<String, Object>>() {};

                Map<String, Object> document = mapper.readValue(new File("apl_album_list_template.json"), documentMapType);
                Map<String, Object> data = mapper.readValue(albumsJson, documentMapType);
                
                // Use builder methods in the SDK to create the directive.
                RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
                        .withToken("AlbumListToken")
                        .withDocument(document)
                        .withDatasources(data)
                        .build();

                // Add the directive to a responseBuilder. 
                responseBuilder.addDirective(renderDocumentDirective);

            } catch (IOException e) {
                throw new AskSdkException("Unable to read or deserialize the APL document", e);
            }
        } else {
            // Change the speech output since the device does not have a screen.
            speechText += " This example would be more interesting on a device with a screen, such as an Echo Show or Fire TV.";
        }

        // add the speech to the response and return it.

        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("Hello World with APL", speechText)
            .build();
	}

}