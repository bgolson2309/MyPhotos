package com.wakeword.interceptors;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.interceptor.ResponseInterceptor;
import com.amazon.ask.model.Response;

import java.util.Optional;

public class ResponseLogInterceptor implements ResponseInterceptor {
    
    public void process(HandlerInput input, Optional<Response> response) {
    	System.out.print("RESPONSE = " + response.toString());
	}

}