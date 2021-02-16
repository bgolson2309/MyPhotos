package com.wakeword.handlers;

import com.wakeword.dto.MediaItem;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.PhotoManager;
import com.wakeword.util.StringUtils;
import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.request.RequestHelper;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.viewport.ViewportState;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import static com.amazon.ask.request.Predicates.intentName;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FilterImagesIntentHandler implements IntentRequestHandler  {
    
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return (input.matches(intentName(Constants.FILTER_IMAGES_INTENT)));
	}
	
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
        ResponseBuilder responseBuilder = input.getResponseBuilder();
    	Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
		ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String imagesResponse, speechText, imagesJson = null;
		RequestHelper requestHelper = RequestHelper.forHandlerInput(input);
		//get the slot ID for the Google API
		String category = requestHelper.getSlot(Constants.CATEGORY).get().getResolutions().getResolutionsPerAuthority().get(0).getValues().get(0).getValue().getId();
    	Optional<String> spokenCategory = requestHelper.getSlotValue(Constants.CATEGORY);
		String[] categories = {category};
    	
    	if (googleToken == null )
    	{
            speechText = "Please use the Alexa application to link your Google account with My Photos.";
            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .withLinkAccountCard()
                    .build();    	
        } else {
        		ObjectMapper objectMapper = new ObjectMapper();      		
    			try {
            		imagesResponse = PhotoManager.searchMediaByCategories(googleToken, categories);
       			 	MediaItem[] media = objectMapper.readValue(imagesResponse.substring(17), MediaItem[].class); 
       			    sessionAttributes.put("IMAGE_UUID_LIST", StringUtils.makeImageList(media));

       			 	imagesJson = AplUtil.buildPhotoData(media, currentPixelWidth, currentPixelHeight, "Photos filtered by " + spokenCategory.get());
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	speechText = "Here's your photos filtered by category " + spokenCategory.get();
    	    	
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
            speechText = "My Photos is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
        }
        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("My Photos", speechText)
            .build();
	}
}