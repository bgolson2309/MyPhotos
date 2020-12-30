package com.wakeword.handlers;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.UserEventHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.UserEvent;
import com.amazon.ask.model.interfaces.viewport.Shape;
import com.amazon.ask.model.interfaces.viewport.ViewportState;

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
    public Optional<Response> handle(HandlerInput input, UserEvent userEvent) {
    	  ViewportState viewportState = input.getRequestEnvelope().getContext().getViewport();
    	  Shape shape = viewportState.getShape();
    	  int currentPixelWidth = viewportState.getCurrentPixelWidth().intValueExact();
    	  int currentPixelHeight = viewportState.getCurrentPixelHeight().intValueExact();
    	  
    	  String speechText = "Thank you for clicking the button! I imagine you already noticed that the text faded away. Tell me to start over to bring it back!";
        
        return input.getResponseBuilder()
            .withSpeech(speechText)
            .withReprompt("Tell me to start over if you want me to bring the text back into view. Or, you can just say hello again.")
            .build();
    }
}