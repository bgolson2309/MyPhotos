package com.wakeword.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.UserEventHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.UserEvent;
import com.amazon.ask.model.interfaces.viewport.ViewportState;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeword.dto.Album;
import com.wakeword.dto.MediaItem;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;

public class GoBackEventHandler implements UserEventHandler {

    @SuppressWarnings("rawtypes")
	@Override
    public boolean canHandle(HandlerInput input, UserEvent userEvent) {      
        ArrayList argumentsObject =  (ArrayList) userEvent.getArguments();
        String eventSourceId = (String) argumentsObject.get(0);
        return eventSourceId.equals("goBack");
    }

    @SuppressWarnings("rawtypes")
	@Override
    /*
     * Arguments[] passed in from my UI:
     * 0 = User Event Type for checking in the canHandle()
     * 1 = Album Title
     * 2 = Album ID for making Google API call for Media Items
     */
    public Optional<Response> handle(HandlerInput input, UserEvent userEvent) {
    	 
        ResponseBuilder responseBuilder = input.getResponseBuilder();
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
        
        String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String albumsString, speechText, albumsJson = null;

    	if (googleToken == null )
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
    	    	speechText = "Here's your list of albums again.";
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
            speechText = "My Photos is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
        }

        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("My Photos", speechText)
            .build();
	}
}