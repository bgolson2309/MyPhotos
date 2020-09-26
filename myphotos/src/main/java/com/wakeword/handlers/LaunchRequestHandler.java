package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.wakeword.dto.Customer;
import com.wakeword.dto.CustomerManager;
import com.wakeword.main.Constants;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;
public class LaunchRequestHandler implements RequestHandler  {
    private static Logger LOG = getLogger(LaunchRequestHandler.class);
    public boolean canHandle(HandlerInput input) {
        return input.matches(requestType(LaunchRequest.class));
    }
    public Optional<Response> handle(HandlerInput input) {
        
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	LOG.debug("GOOGLE TOKEN = " + googleToken);
		//String awsConsentToken = input.getRequestEnvelope().getContext().getSystem().getApiAccessToken();
    	String userId = input.getRequestEnvelope().getContext().getSystem().getUser().getUserId();
	 	CustomerManager m = new CustomerManager(userId);
	 	boolean customerExists = m.hasCustomer(userId);  // object in S3?

        return input.getResponseBuilder()
                .withSpeech(Constants.FIRST_VISIT)
                .withSimpleCard(Constants.MY_PHOTOS, Constants.FIRST_VISIT)
                .withShouldEndSession(false)
                .build();


	}
}