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
public class SelectedImageIntentHandler implements RequestHandler {

    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName(Constants.SELECT_IMAGE));
    }
    
    public Optional<Response> handle(HandlerInput input) {

        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();

		 ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		 int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		 int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		 String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();

		 RequestHelper requestHelper = RequestHelper.forHandlerInput(input);
		 Optional<String> position = requestHelper.getSlotValue(Constants.IMAGE_POSITION_SLOT);
	 
		 String imageUUID = null;
		 try {
		     if (sessionAttributes.containsKey("IMAGE_UUID_LIST")) {
		    	 imageUUID = StringUtils.getSelectedAlbumUUID(Integer.parseInt(position.get()), sessionAttributes.get("IMAGE_UUID_LIST").toString()); 
		     }
		 } catch (Exception e) {
			 e.printStackTrace();
		 }

		 String selectedImageAPIResponse = PhotoManager.getMediaItem(googleToken, imageUUID);

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
			 e.printStackTrace();
		 }
		 
		 // build response for user
		 ResponseBuilder responseBuilder = input.getResponseBuilder();
		 String speechText = "Here's your selected image.";

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
