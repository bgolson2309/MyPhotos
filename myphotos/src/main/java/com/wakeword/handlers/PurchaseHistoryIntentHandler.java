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

public class PurchaseHistoryIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals(Constants.PURCHASE_HISTORY_INTENT);
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
    	
    	final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();
        
        final List<String> entitledProducts = ISPUtil.getAllEntitledProducts(inSkillProducts);  //Get what's been purchased
        
        if (entitledProducts.size() > 0) {
            return handlerInput.getResponseBuilder()
            		.withSpeech(Constants.YOU_BOUGHT_PREMIUM)
            		.withShouldEndSession(true)
                    .build();
        } else {
            return handlerInput.getResponseBuilder()
            		.withSpeech(Constants.YOU_HAVENT_BOUGHT_PREMIUM)
                    .build();
        }
    }

}
