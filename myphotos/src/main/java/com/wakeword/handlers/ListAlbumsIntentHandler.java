package com.wakeword.handlers;

import com.wakeword.dto.Album;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;
import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.request.RequestHelper;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.amazon.ask.request.Predicates.intentName;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ListAlbumsIntentHandler implements IntentRequestHandler  {
    
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return (input.matches(intentName(Constants.LIST_ALBUMS_INTENT)));
	}
	
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		ResponseBuilder responseBuilder = input.getResponseBuilder();
		AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
    	Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String albumsString, speechText, albumsJson = null;

    	if (googleToken == null || (!PhotoManager.validateToken(googleToken)))
    	{
            speechText = "Please use the Alexa application to link your Google account with My Images.";
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
                	sessionAttributes.put("SESSION_VIEW_MODE", "ALBUMS_VIEW");
       			    sessionAttributes.put("IMAGE_UUID_LIST", "");
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	speechText = "Here's your list of albums.";
    	}
    	
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
            speechText = "My Images is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
        }

        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("My Images", speechText)
            .build();
	}
}