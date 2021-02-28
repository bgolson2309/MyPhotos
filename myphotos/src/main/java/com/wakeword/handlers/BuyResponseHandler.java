package com.wakeword.handlers;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;
import com.amazon.ask.request.RequestHelper;
import com.wakeword.main.Constants;
import com.wakeword.util.ISPUtil;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;
import java.util.List;
import java.util.Optional;

public class BuyResponseHandler implements ConnectionsResponseHandler {
    private static Logger LOG = getLogger(BuyResponseHandler.class);

    private static final String SUCCESS_CODE = "200";

    @Override
    public boolean canHandle(HandlerInput handlerInput, ConnectionsResponse connectionsResponse) {
        final String name = handlerInput.getRequestEnvelopeJson().get(Constants.REQUEST).get(Constants.NAME).asText();
        return name.equalsIgnoreCase(Constants.BUY) || name.equalsIgnoreCase(Constants.UPSELL);
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput, ConnectionsResponse connectionsResponse) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final String productId = handlerInput.getRequestEnvelopeJson().get(Constants.REQUEST).get(Constants.PAYLOAD).get(Constants.PRODUCT_ID).asText();

        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();

        final Optional<InSkillProduct> inSkillProduct = ISPUtil.getInSkillProduct(inSkillProducts, productId);
        final String code = handlerInput.getRequestEnvelopeJson().get(Constants.REQUEST).get(Constants.STATUS).get(Constants.CODE).asText();
        if (inSkillProduct.isPresent() && code.equalsIgnoreCase(SUCCESS_CODE)) {
            final String purchaseResult = handlerInput.getRequestEnvelopeJson().get(Constants.REQUEST).get(Constants.PAYLOAD).get(Constants.PURCHASE_RESULT).asText();
                                          
            LOG.debug(">> Purchase Result is: " + purchaseResult);
            
            switch (purchaseResult) {
                case "ACCEPTED": {
                    return handlerInput.getResponseBuilder()
                            .withSpeech("You just purchased Premium Access, and can begin to enjoy rich image category filtering, searching, viewing and navigation.")
                            .build();                 }
                case "DECLINED": {
                	// user said "no" to buying when being asked to confirm and changed mind during the purchase flow.  Alexa will say "ok".  Then this follows...
                    return handlerInput.getResponseBuilder()
                            .withSpeech("Perhaps another time.")
                            .build();
                }
                case "ALREADY_PURCHASED": {
                    return handlerInput.getResponseBuilder()
                            .withSpeech("You previously purchased Premium Access, and can already enjoy rich image category filtering, searching, viewing and navigation.")
                            .build();                }
                default:
                    return handlerInput.getResponseBuilder()
                            .withSpeech("Something unexpected happened, but thanks for your interest in premium access.")
                            .build();
            }
        }
        //Something failed
        LOG.debug(String.format("Connections.Response indicated failure. error: %s", handlerInput.getRequestEnvelopeJson().get("request").get("status").get("message").toString()));
        return handlerInput.getResponseBuilder()
                .withSpeech("There was an error handling your purchase request. Please try again or contact us for help.")
                .build();


    }

}
