package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.wakeword.main.Constants;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
public class FallbackIntentHandler implements RequestHandler {

    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName(Constants.AMAZON_FALLBACK_INTENT));
    }
    public Optional<Response> handle(HandlerInput input) {
        String speechText = Constants.HELP;
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withSimpleCard(Constants.MY_PHOTOS, speechText)
                .withReprompt(speechText)
                .build();
    }
}