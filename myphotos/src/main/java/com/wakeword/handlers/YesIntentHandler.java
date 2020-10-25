package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.wakeword.main.Constants;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
public class YesIntentHandler implements RequestHandler {

    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName(Constants.AMAZON_YES_INTENT));
    }
    public Optional<Response> handle(HandlerInput input) {
    	String sessionKey = "AlbumList";
    	String albumList = (String) input.getAttributesManager().getSessionAttributes().get(sessionKey);
    	String speechText = "The length of data in Album List is" + albumList.length();
    	
    	//String speechText = Constants.NO_RESPONSE;
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(Constants.MY_PHOTOS, speechText)
                .build();
    }
}
