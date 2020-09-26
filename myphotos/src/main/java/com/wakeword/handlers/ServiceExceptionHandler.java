package com.wakeword.handlers;

import com.wakeword.main.Constants;
import com.amazon.ask.dispatcher.exception.ExceptionHandler;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.ServiceException;
import java.util.Arrays;
import java.util.Optional;


public class ServiceExceptionHandler implements ExceptionHandler {
    public boolean canHandle(HandlerInput handlerInput, Throwable throwable) {
        return throwable instanceof ServiceException;
    }

    public Optional<Response> handle(HandlerInput handlerInput, Throwable throwable) {
        ServiceException e = (ServiceException)throwable;
        
        System.out.print("Error message : " + throwable.getMessage());
        System.out.print("Error status code = " + e.getStatusCode());

        if (e.getStatusCode() == 403) {
          return handlerInput.getResponseBuilder()
          .withSpeech(Constants.NOTIFY_MISSING_PERMISSIONS)
          .withAskForPermissionsConsentCard(Arrays.asList(Constants.NAME_PERMISSION, Constants.EMAIL_PERMISSION))
          .build();
          // hard lesson: match consent card perms, with perms for the skill exactly
        }
        return handlerInput.getResponseBuilder()
                .withSpeech(Constants.API_FAILURE)
                .withReprompt(Constants.API_FAILURE)
                .build();
    }
}
