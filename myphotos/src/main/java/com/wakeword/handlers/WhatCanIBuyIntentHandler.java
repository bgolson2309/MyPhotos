package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.request.RequestHelper;
import com.wakeword.main.Constants;
import com.wakeword.util.ISPUtil;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;

import java.util.List;
import java.util.Optional;

public class WhatCanIBuyIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals(Constants.WHAT_CAN_I_BUY_INTENT);
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();
        final List<String> availableProducts = ISPUtil.getListOfAvailableProducts(inSkillProducts);
        
        if (availableProducts.size() > 0) {
            final String speechText = String.format(Constants.WHAT_CAN_I_BUY,
            		ISPUtil.getSpeakableListOfProducts(availableProducts));
            
            return handlerInput.getResponseBuilder()
                    .withSpeech(speechText)
                    .withReprompt(Constants.HUH) //triggers in a few seconds if no response - on a real device
                    .build();
        }
        return handlerInput.getResponseBuilder()
                .withSpeech(Constants.NO_ISP)
                .build();
    }

}
