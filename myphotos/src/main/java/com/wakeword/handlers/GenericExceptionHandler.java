package com.wakeword.handlers;

import com.wakeword.main.Constants;
import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;

import java.util.Optional;

public class GenericExceptionHandler implements ExceptionHandler {
    public boolean canHandle(HandlerInput handlerInput, Throwable throwable) {
        return true;
    }

    public Optional<Response> handle(HandlerInput handlerInput, Throwable throwable) {
        throwable.printStackTrace();
        return handlerInput.getResponseBuilder()
                .withSpeech(Constants.ERROR)
                .build();
    }
}
