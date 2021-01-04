package com.wakeword.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.UserEventHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.UserEvent;
import com.amazon.ask.model.interfaces.viewport.Shape;
import com.amazon.ask.model.interfaces.viewport.ViewportState;
import com.amazon.ask.request.RequestHelper;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeword.dto.MediaItem;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;

public class AlbumImagesEventHandler implements UserEventHandler {

    @Override
    public boolean canHandle(HandlerInput input, UserEvent userEvent) {      
        // This is a typed handler, so it only runs for UserEvents. Since an APL skill might have multiple controls that generate UserEvents,
        // use the an argument to track the control source - in this case the AlbumImageList 
        ArrayList argumentsObject =  (ArrayList) userEvent.getArguments();
        String eventSourceId = (String) argumentsObject.get(0);
        return eventSourceId.equals("AlbumListItemSelected");
    }

    @Override
    /*
     * Arguments[] passed in:
     * 0 = User Event Type
     * 1 = ALbum Title
     * 2 = Album ID for making Google API call for Media Items
     */
    public Optional<Response> handle(HandlerInput input, UserEvent userEvent) {
    	 
    	 // get viewport info
		 ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		 Shape shape = viewportState.getShape();
		 int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		 int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		 
		 // get selected album id and make Google API call
		 String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
		 ArrayList argumentsObject =  (ArrayList) userEvent.getArguments();
		 String albumTitle = (String) argumentsObject.get(1);
	     String albumId = (String) argumentsObject.get(2);
		 String selectedAlbumAPIResponse = PhotoManager.listAlbumMedia(googleToken, albumId);

 		String selectedAlbum = PhotoManager.listAlbumMedia(googleToken, "AMEXHWpANbSolnXXxx5o9BWI7vGh8miF-c_27A6Z_mM6IXNPP6B_Of7d6N7ZjvKv4jP657jtEWoj");
 		System.out.println("UTAH ALBUM STRING = " + selectedAlbum);
		 
		 //build json data of MediaItems for selected album
	    String photosJson = null;
		ObjectMapper objectMapper = new ObjectMapper();      		

		 try {
			 MediaItem[] media = objectMapper.readValue(selectedAlbumAPIResponse.substring(17), MediaItem[].class); 
        	photosJson = AplUtil.buildPhotoData(media, currentPixelWidth, currentPixelHeight, albumTitle);
        		
	    } catch (Exception e) {
	    	System.out.println(e.getMessage());
	    }
		System.out.println("PHOTOS JSON = " + photosJson);
		 
		 // build response for user
		 ResponseBuilder responseBuilder = input.getResponseBuilder();
 
		 String speechText = "Nice Photos!";
      	// Check for APL support on the user's device
          if (RequestHelper.forHandlerInput(input)
                  .getSupportedInterfaces()
                  .getAlexaPresentationAPL() != null) {

              // Code to send APL directives can go here
              try {
                  // Retrieve the JSON document and put into a string/object map
                  ObjectMapper mapper = new ObjectMapper();
                  TypeReference<HashMap<String, Object>> documentMapType = 
                      new TypeReference<HashMap<String, Object>>() {};

                  Map<String, Object> document = mapper.readValue(new File("apl_image_list_template.json"), documentMapType);
                  Map<String, Object> data = mapper.readValue(photosJson, documentMapType);

                  // Use builder methods in the SDK to create the directive.
                  RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
                          .withToken("ImageListToken")
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
              .withReprompt("Tell me to start over if you want me to bring the text back into view. Or, you can just say hello again.")
              .build();
    	  
    }
}