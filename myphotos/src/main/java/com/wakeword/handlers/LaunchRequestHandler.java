package com.wakeword.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.canfulfill.CanFulfillIntent;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.viewport.ViewportState;
import com.amazon.ask.response.ResponseBuilder;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.ISPUtil;
import com.wakeword.util.PhotoManager;
import com.wakeword.util.StringUtils;

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
    			System.out.println("ALBUM STRING LEN = " + albumsString.length());
    			if (albumsString.length() > 100) {
    				
	    			try {
	            		Album[] albums = objectMapper.readValue(albumsString.substring(13), Album[].class); 
	       			 	sessionAttributes.put("ALBUM_UUID_LIST", StringUtils.makeAlbumList(albums));
	       			 	sessionAttributes.put("ALBUM_TITLE_LIST", StringUtils.makeAlbumTitleList(albums));
	                	sessionAttributes.put("SESSION_VIEW_MODE", "ALBUMS_VIEW");
	       			    sessionAttributes.put("IMAGE_UUID_LIST", "");
	            		albumsJson = AplUtil.buildAlbumData(albums);
	    	    	} catch (Exception e) {
	    	    		System.out.println(e.getMessage());
	    	    	}
    			} else {
    				return handleNoAlbums(input);
    			}
    				
    	    	speechText = "Welcome to My Images.";
    	}
    	
    	// This saves the attributes to the session
    	attributesManager.setSessionAttributes(sessionAttributes);
    	// ...and this saves long term attributes to Dynamo
    	input.getAttributesManager().setPersistentAttributes(persistentAttributes);
    	input.getAttributesManager().savePersistentAttributes(); 

	
    	
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
    
	private Optional<Response> handleNoAlbums(HandlerInput input) {
        ResponseBuilder responseBuilder = input.getResponseBuilder();
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
		ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String imagesResponse, speechText, imagesJson = null;

    	if (googleToken == null )
    	{
            speechText = "Please use the Alexa application to link your Google account with My Images.";
            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .withLinkAccountCard()
                    .build();    	
        } else {
        		ObjectMapper objectMapper = new ObjectMapper();      		
    			try {
            		imagesResponse = PhotoManager.listMedia(googleToken);
       			 	MediaItem[] media = objectMapper.readValue(imagesResponse.substring(17), MediaItem[].class); 
       			 	imagesJson = AplUtil.buildPhotoData(media, currentPixelWidth, currentPixelHeight, "Your most recent photos");
                	sessionAttributes.put("SESSION_VIEW_MODE", "IMAGE_LIST_VIEW");
       			    sessionAttributes.put("IMAGE_UUID_LIST", StringUtils.makeImageList(media));
                	sessionAttributes.put("IMAGE_SEARCH_BY_TYPE", "NONE");
                	attributesManager.setSessionAttributes(sessionAttributes);
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	speechText = "Here's your most recent images.";
    	}
    	
		 if (AplUtil.supportsApl(input)) {
             try {
                 // Retrieve the JSON document and put into a string/object map
                 ObjectMapper mapper = new ObjectMapper();
                 TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() {};
                 Map<String, Object> document = mapper.readValue(new File("apl_image_list_template.json"), documentMapType);
                 Map<String, Object> data = mapper.readValue(imagesJson, documentMapType);

                 // Use builder methods in the SDK to create the directive.
                 RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
                         .withToken("ImageListToken")
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