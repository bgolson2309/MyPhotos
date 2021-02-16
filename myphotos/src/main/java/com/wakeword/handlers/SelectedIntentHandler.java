package com.wakeword.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.viewport.ViewportState;
import com.amazon.ask.request.RequestHelper;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeword.dto.Album;
import com.wakeword.dto.MediaItem;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;
import com.wakeword.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
public class SelectedIntentHandler implements RequestHandler {

    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName(Constants.SELECT_ALBUM));
    }
    
    public Optional<Response> handle(HandlerInput input) {

         AttributesManager attributesManager = input.getAttributesManager();
         Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();

		 ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		 int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		 int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		 String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();

		 RequestHelper requestHelper = RequestHelper.forHandlerInput(input);
		 Optional<String> position = requestHelper.getSlotValue(Constants.LIST_POSITION_SLOT);
	 
		 String albumTitle = null;
		 String albumId = null;
		 try {
		     if (sessionAttributes.containsKey("ALBUM_UUID_LIST")) {
		   	  	albumId = StringUtils.getSelectedAlbumUUID(Integer.parseInt(position.get()), sessionAttributes.get("ALBUM_UUID_LIST").toString()); 
				sessionAttributes.put("SELECTED_ALBUM_UUID", albumId );
		     }
		     if (sessionAttributes.containsKey("ALBUM_TITLE_LIST")) {
		    	 albumTitle = StringUtils.getSelectedAlbumUUID(Integer.parseInt(position.get()), sessionAttributes.get("ALBUM_TITLE_LIST").toString()); 
			 }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 String selectedAlbumAPIResponse = PhotoManager.listAlbumMedia(googleToken, albumId);
		 //build json data of MediaItems for the selected album
		 String photosJson = null;
		 ObjectMapper objectMapper = new ObjectMapper();      		

		 try {
			 MediaItem[] media = objectMapper.readValue(selectedAlbumAPIResponse.substring(17), MediaItem[].class); 
			 sessionAttributes.put("IMAGE_UUID_LIST", StringUtils.makeImageList(media));
			 photosJson = AplUtil.buildPhotoData(media, currentPixelWidth, currentPixelHeight, albumTitle);
		 } catch (Exception e) {
	    	System.out.println(e.getMessage());
		 }
		 
		 // build response for user
		 ResponseBuilder responseBuilder = input.getResponseBuilder();
		 String speechText = "Nice Images!";
		 if (AplUtil.supportsApl(input)) {
             try {
                 // Retrieve the JSON document and put into a string/object map
                 ObjectMapper mapper = new ObjectMapper();
                 TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() {};
                 Map<String, Object> document = mapper.readValue(new File("apl_image_list_template.json"), documentMapType);
                 Map<String, Object> data = mapper.readValue(photosJson, documentMapType);

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
            speechText = "My Photos is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
        }
        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("My Photos", speechText)
            .build();
	}
}
