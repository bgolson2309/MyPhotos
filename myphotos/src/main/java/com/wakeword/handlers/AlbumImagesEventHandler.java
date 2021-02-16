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
import com.wakeword.dto.MediaItem;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;
import com.wakeword.util.StringUtils;

public class AlbumImagesEventHandler implements UserEventHandler {

    @SuppressWarnings("rawtypes")
	@Override
    public boolean canHandle(HandlerInput input, UserEvent userEvent) {      
        // This is a typed handler for UserEvents. An APL skill might have multiple controls that generate UserEvents, use an argument to track the control source - in this case the AlbumImageList 
        ArrayList argumentsObject =  (ArrayList) userEvent.getArguments();
        String eventSourceId = (String) argumentsObject.get(0);
        return eventSourceId.equals("AlbumListItemSelected");
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
         AttributesManager attributesManager = input.getAttributesManager();
         Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
         
    	 // get viewport info
		 ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		 int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		 int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		 
		 // get selected album id and make Google API call
		 String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
		 ArrayList argumentsObject =  (ArrayList) userEvent.getArguments();
		 String albumTitle = (String) argumentsObject.get(1);
	     String albumId = (String) argumentsObject.get(2);
		 sessionAttributes.put("SELECTED_ALBUM_UUID", albumId );

		 String selectedAlbumAPIResponse = PhotoManager.listAlbumMedia(googleToken, albumId);
 
		 //build json data of MediaItems for the selected album
		 String photosJson = null;
		 ObjectMapper objectMapper = new ObjectMapper();      		

		 try {
			 MediaItem[] media = objectMapper.readValue(selectedAlbumAPIResponse.substring(17), MediaItem[].class); 
			 sessionAttributes.put("IMAGE_UUID_LIST", StringUtils.makeImageList(media));
			 photosJson = AplUtil.buildPhotoData(media, currentPixelWidth, currentPixelHeight, albumTitle);
		 } catch (Exception e) {
	    	e.printStackTrace();
		 }
		 
		 // build response for user
		 ResponseBuilder responseBuilder = input.getResponseBuilder();
 		 String speechText = "Nice Photos!";
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
             speechText = "My Images is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
         }
         // add the speech to a simple card response and return it for the case of a device w/out a screen.
         return responseBuilder
             .withSpeech(speechText)
             .withSimpleCard("My Images", speechText)
             .build();
    	  
    }
}