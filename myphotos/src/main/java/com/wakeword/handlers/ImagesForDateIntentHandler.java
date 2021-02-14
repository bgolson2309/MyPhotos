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

        ResponseBuilder responseBuilder = input.getResponseBuilder();
    	Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();

		Optional<String> duration = requestHelper.getSlotValue(Constants.DURATION_SLOT);
		Duration d = Duration.parse(DurationUtils.revisedDays(duration.get()));
		long numDays = d.toDays();
		
    	String speechText = "Duration is " + numDays + " days.";

    	

        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .withSimpleCard("My Images", speechText)
            .build();
	}
}