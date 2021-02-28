package com.wakeword.handlers;

import com.wakeword.main.Constants;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import java.util.Optional;

public class UnhandledIntentHandler implements RequestHandler {

    public boolean canHandle(HandlerInput handlerInput) {
        return true;
    } 

    public Optional<Response> handle(HandlerInput handlerInput) {

    	try {
        	String cancelResult = handlerInput.getRequestEnvelopeJson().get(Constants.REQUEST).get(Constants.PAYLOAD).get(Constants.PURCHASE_RESULT).asText();
        	if (cancelResult != null) {
    	    	switch (cancelResult) {
    		         case "ACCEPTED": {
    			      	  	return handlerInput.getResponseBuilder()
    		                     .withSpeech(Constants.CANCEL_ACCEPTED)
    			                 .withShouldEndSession(true)
    			                 .build();
    		         }
    		         default:
    		             return handlerInput.getResponseBuilder()
    		                     .withSpeech(Constants.UNHANDLED)
    		                     .withReprompt(Constants.UNHANDLED)
    		                     .build();
    	    	 }
        	} else {
                return handlerInput.getResponseBuilder()
                        .withSpeech(Constants.UNHANDLED)
                        .withReprompt(Constants.UNHANDLED)
                        .build();
        	}
    	}
    	catch (Exception e)  {
            return handlerInput.getResponseBuilder()
                    .withSpeech(Constants.UNHANDLED)
                    .withReprompt(Constants.UNHANDLED)
                    .build();
    	}
   }
}
