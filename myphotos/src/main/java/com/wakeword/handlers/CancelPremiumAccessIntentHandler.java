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
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import java.util.List;
import java.util.Optional;

public class CancelPremiumAccessIntentHandler implements IntentRequestHandler {
    private static Logger LOG = getLogger(CancelPremiumAccessIntentHandler.class);

    @Override
    public boolean canHandle(HandlerInput handlerInput, IntentRequest intentRequest) {
        return intentRequest.getIntent().getName().equals(Constants.CANCEL_PREMIUM_ACCESS_INTENT);
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, IntentRequest intentRequest) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> products = response.getInSkillProducts();
        final Optional<InSkillProduct> premiumSubscriptionProduct = ISPUtil.getPremiumAccessProduct(products);

        if(premiumSubscriptionProduct.isPresent()) {
            //Send Connections.SendRequest Directive back to Alexa to switch to Refund Flow
            return handlerInput.getResponseBuilder()
                    .addDirective(ISPUtil.getDirectiveByType(premiumSubscriptionProduct.get().getProductId(), Constants.CANCEL))
                    .build();
        }
        
        return handlerInput.getResponseBuilder()
                .withSpeech(String.format(Constants.NO_ISP))
                .build();
    }

}
