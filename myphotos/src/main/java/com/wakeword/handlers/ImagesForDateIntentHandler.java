package com.wakeword.handlers;

import com.wakeword.dto.MediaItem;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.DurationUtils;
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
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ImagesForDateIntentHandler implements IntentRequestHandler  {
    
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return (input.matches(intentName(Constants.IMAGES_FOR_DATE_INTENT)));
	}
	
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		RequestHelper requestHelper = RequestHelper.forHandlerInput(input);

		ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
		int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
		int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
		
        ResponseBuilder responseBuilder = input.getResponseBuilder();
    	Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();

		Optional<String> duration = requestHelper.getSlotValue(Constants.DURATION_SLOT);
		Duration d = Duration.parse(DurationUtils.revisedDays(duration.get()));
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String imagesResponse, speechText, imagesJson = null;

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
            		imagesResponse = PhotoManager.searchMediaByDuration(googleToken, d);
       			 	MediaItem[] media = objectMapper.readValue(imagesResponse.substring(17), MediaItem[].class); 
       			    sessionAttributes.put("IMAGE_UUID_LIST", StringUtils.makeImageList(media));

       			 	imagesJson = AplUtil.buildPhotoData(media, currentPixelWidth, currentPixelHeight, "Images filtered by date");
    	    	} catch (Exception e) {
    	    		e.printStackTrace();
    	    	}
    	    	speechText = "Here's your images filtered by date.";
    	    	
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