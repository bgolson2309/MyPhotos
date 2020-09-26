package com.wakeword.interceptors;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.interceptor.RequestInterceptor;

public class RequestLogInterceptor implements RequestInterceptor {
    
	public void process(HandlerInput input) {
		System.out.print("REQUEST ENVELOP = " + input.getRequestEnvelope().toString());
		System.out.print("SESSION ATTRIBUTES = " + input.getAttributesManager().getSessionAttributes().toString());
	}

}
