package com.wakeword.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

public class SelectedImageEventHandler implements UserEventHandler {

    @SuppressWarnings("rawtypes")
	@Override
    public boolean canHandle(HandlerInput input, UserEvent userEvent) {      
        // This is a typed handler for UserEvents. An APL skill might have multiple controls that generate UserEvents, use an argument to track the control source - in this case the AlbumImageList 
        ArrayList argumentsObject =  (ArrayList) userEvent.getArguments();
        String eventSourceId = (String) argumentsObject.get(0);
        return (eventSourceId.equals("ImageListItemSelected") || eventSourceId.equals("PrevButton") || eventSourceId.equals("NextButton"));
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
		 String imageUUID = null;
		 String eventSourceId = (String) argumentsObject.get(0);
		 try {
			 if (eventSourceId.equals("ImageListItemSelected")) { 
				 imageUUID = (String) argumentsObject.get(1);
			 } else {
			     if (sessionAttributes.containsKey("IMAGE_UUID_LIST")) {
			    	 imageUUID = StringUtils.getNextImageUUID(eventSourceId, sessionAttributes.get("IMAGE_UUID_LIST").toString(), sessionAttributes.get("SESSION_SELECTED_IMAGE_UUID").toString()); 
			     }
			 }
		 } catch (Exception e) {
			 System.out.println(e.getStackTrace());
		 }

		 String selectedImageAPIResponse = PhotoManager.getMediaItem(googleToken, imageUUID);
		 System.out.println("SelectedImageAPIResponse = " + selectedImageAPIResponse);

		 //build json data of MediaItems for the selected album
		 String photoJson = null;
		 MediaItem media = null;
		 ObjectMapper objectMapper = new ObjectMapper();      		

		 try {
			 media = objectMapper.readValue(selectedImageAPIResponse, MediaItem.class); 
			 photoJson = AplUtil.buildSelectedMediaData(media, currentPixelWidth, currentPixelHeight);
         	 sessionAttributes.put("SESSION_VIEW_MODE", "IMAGE_ITEM_VIEW");
         	 sessionAttributes.put("SESSION_SELECTED_IMAGE_UUID", imageUUID);
        	 attributesManager.setSessionAttributes(sessionAttributes);
		 } catch (Exception e) {
	    	System.out.println(e.getMessage());
		 }
		 
		 // build response for user
		 ResponseBuilder responseBuilder = input.getResponseBuilder();
		 String speechText = null;
		 if (eventSourceId.equals("ImageListItemSelected")) {
			 speechText = "Here's your selected photo.";
		 }
 		 if (AplUtil.supportsApl(input)) {
              try {
                  // Retrieve the JSON document and put into a string/object map
                  ObjectMapper mapper = new ObjectMapper();
                  TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() {};
                  Map<String, Object> document = null;
                  if (Integer.parseInt(media.getMediaMetadata().getWidth()) > Integer.parseInt(media.getMediaMetadata().getHeight())) {
                	  document = mapper.readValue(new File("apl_image_view_wide_template.json"), documentMapType);
                  } else {
                	  document = mapper.readValue(new File("apl_image_view_template.json"), documentMapType);
                  }
                  Map<String, Object> data = mapper.readValue(photoJson, documentMapType);

                  RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
                          .withToken("ViewImageToken")
                          .withDocument(document)
                          .withDatasources(data)
                          .build();
                  if (eventSourceId.equals("ImageListItemSelected")) {
	                  return input.getResponseBuilder()
	                          .withSpeech(speechText)
	                          .addDirective(renderDocumentDirective)
	                          .build();
                  } else {
	                  return input.getResponseBuilder()
	                          .addDirective(renderDocumentDirective)
	                          .build();
                  }

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