package com.wakeword.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.AutoPageCommand;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.AutoPageCommand.Builder;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.ExecuteCommandsDirective;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.viewport.ViewportState;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeword.dto.MediaItem;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.DurationUtils;
import com.wakeword.util.ISPUtil;
import com.wakeword.util.PhotoManager;
import com.wakeword.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
public class SlideShowIntentHandler implements RequestHandler {

    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName(Constants.SLIDE_SHOW_INTENT));
    }
    public Optional<Response> handle(HandlerInput input) {
       	if (ISPUtil.hasPremiumAccess(input)) {	
    	
		   	 AttributesManager attributesManager = input.getAttributesManager();
		     Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
		    
			 // get viewport info
			 ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
			 int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
			 int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
			 
			 // get selected album id and make Google API call
			 String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
			 String albumId = null;
			 String apiResponse = null;
			 String photosJson = null;
			 ObjectMapper objectMapper = new ObjectMapper(); 
		
		     if (sessionAttributes.containsKey("SELECTED_ALBUM_UUID")) {
		    	 albumId = sessionAttributes.get("SELECTED_ALBUM_UUID").toString(); 
		    	 apiResponse = PhotoManager.listAlbumMedia(googleToken, albumId);
		     } else {
		    	 if (sessionAttributes.containsKey("SESSION_VIEW_MODE")) {
		    		 String viewMode = sessionAttributes.get("SESSION_VIEW_MODE").toString();
		    		 if (viewMode.equals("IMAGE_LIST_VIEW")) {
		    			 if (sessionAttributes.containsKey("IMAGE_SEARCH_BY_TYPE") && sessionAttributes.containsKey("IMAGE_SEARCH_BY_VALUE") && sessionAttributes.get("IMAGE_SEARCH_BY_TYPE").toString().equals("FILTER") ) {
		    				 // get media by filter category
		    				 String category = sessionAttributes.get("IMAGE_SEARCH_BY_VALUE").toString();
		    				 String[] categories = {category};
		    				 apiResponse = PhotoManager.searchMediaByCategories(googleToken, categories);
		    			 } else if (sessionAttributes.containsKey("IMAGE_SEARCH_BY_TYPE") && sessionAttributes.containsKey("IMAGE_SEARCH_BY_VALUE") && sessionAttributes.get("IMAGE_SEARCH_BY_TYPE").toString().equals("DURATION") ) {
		    				 // get media by duration
		    				 String duration = sessionAttributes.get("IMAGE_SEARCH_BY_VALUE").toString();
		    				 Duration d = Duration.parse(DurationUtils.revisedDays(duration));
		    				 apiResponse = PhotoManager.searchMediaByDuration(googleToken, d);
		    			 } else {
		    				 // get media by nothing
		    				 apiResponse = PhotoManager.listMedia(googleToken);
		    			 }
		    		 }
		    	 }
		     }
		
			 try {
				 MediaItem[] media = objectMapper.readValue(apiResponse.substring(17), MediaItem[].class);
				 photosJson = AplUtil.buildSlideshowData(media, currentPixelWidth, currentPixelHeight);
			 } catch (Exception e) {
		    	e.printStackTrace();
			 }
		
			 // build response for user
			 ResponseBuilder responseBuilder = input.getResponseBuilder();
			 String speechText = "Sit back, and enjoy your slideshow.";
		
			 if (AplUtil.supportsApl(input)) {
		          try {
		              // Retrieve the JSON document and put into a string/object map
		              ObjectMapper mapper = new ObjectMapper();
		              TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() {};
		              Map<String, Object> document = null;
		              document = mapper.readValue(new File("apl_slideshow_template.json"), documentMapType);
		              Map<String, Object> data = mapper.readValue(photosJson, documentMapType);
		
		              RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
		                      .withToken("SlideShowToken")
		                      .withDocument(document)
		                      .withDatasources(data)
		                      .build();
		
		              List<com.amazon.ask.model.interfaces.alexa.presentation.apl.Command> commands=new ArrayList<>();
		              commands.add(AutoPageCommand.builder().withComponentId("myPager").withDelay(3000).withDuration(5000).build());
		              
		              ExecuteCommandsDirective executeCommandsDirective = ExecuteCommandsDirective.builder()
		                      .withToken("SlideShowToken")
		                      .withCommands(commands)
		                      .build();
		              
		                  return input.getResponseBuilder()
		                          .addDirective(renderDocumentDirective)
		                          .addDirective(executeCommandsDirective)
		                          .withSpeech(speechText)
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
		} else {
            return input.getResponseBuilder()
            		.withSpeech(Constants.YOU_HAVENT_BOUGHT_PREMIUM)
                    .build();
		}
    }
}
