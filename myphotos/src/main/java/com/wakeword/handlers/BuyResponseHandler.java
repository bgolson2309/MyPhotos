package com.wakeword.handlers;

import com.amazon.ask.attributes.AttributesManager;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.ConnectionsResponseHandler;
import com.amazon.ask.exception.AskSdkException;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.alexa.presentation.apl.RenderDocumentDirective;
import com.amazon.ask.model.interfaces.connections.ConnectionsResponse;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;
import com.amazon.ask.request.RequestHelper;
import com.amazon.ask.response.ResponseBuilder;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wakeword.dto.Album;
import com.wakeword.main.Constants;
import com.wakeword.util.AplUtil;
import com.wakeword.util.ISPUtil;
import com.wakeword.util.PhotoManager;
import com.wakeword.util.StringUtils;

import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
                    return handleGoHomeResponse(handlerInput, connectionsResponse);
                    		}
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
    
    private Optional<Response> handleGoHomeResponse(HandlerInput input, ConnectionsResponse connectionsResponse) { 

        ResponseBuilder responseBuilder = input.getResponseBuilder();
        AttributesManager attributesManager = input.getAttributesManager();
        Map<String,Object> sessionAttributes = attributesManager.getSessionAttributes();
    	Map<String, Object> persistentAttributes = input.getAttributesManager().getPersistentAttributes();
    	
    	String googleToken = input.getRequestEnvelope().getContext().getSystem().getUser().getAccessToken();
    	String albumsString, speechText, albumsJson = null;

    	if (googleToken == null || (!PhotoManager.validateToken(googleToken)))
    	{
            speechText = "Please use the Alexa application to link your Google account with My Photos.";
            return input.getResponseBuilder()
                    .withSpeech(speechText)
                    .withLinkAccountCard()
                    .build();    	
        } else {
        		ObjectMapper objectMapper = new ObjectMapper();      		
    			albumsString = PhotoManager.listAlbums(googleToken);
    			try {
            		Album[] albums = objectMapper.readValue(albumsString.substring(13), Album[].class); 
       			 	sessionAttributes.put("ALBUM_UUID_LIST", StringUtils.makeAlbumList(albums));
       			 	sessionAttributes.put("ALBUM_TITLE_LIST", StringUtils.makeAlbumTitleList(albums));
                	sessionAttributes.put("SESSION_VIEW_MODE", "ALBUMS_VIEW");
       			    sessionAttributes.put("IMAGE_UUID_LIST", "");
            		albumsJson = AplUtil.buildAlbumData(albums);
    	    	} catch (Exception e) {
    	    		System.out.println(e.getMessage());
    	    	}
    	    	speechText = "You just purchased Premium Access, and can begin to enjoy rich image category filtering, searching, viewing and navigation.";
    	}
    	
    	// This saves the attributes to the session
    	attributesManager.setSessionAttributes(sessionAttributes);
    	// ...and this saves long term attributes to Dynamo
    	input.getAttributesManager().setPersistentAttributes(persistentAttributes);
    	input.getAttributesManager().savePersistentAttributes(); 

	
    	
        if (AplUtil.supportsApl(input)) {
            try {
                // Retrieve the JSON document and put into a string/object map
                ObjectMapper mapper = new ObjectMapper();
                TypeReference<HashMap<String, Object>> documentMapType = new TypeReference<HashMap<String, Object>>() {};
                Map<String, Object> document = mapper.readValue(new File("apl_album_list_template.json"), documentMapType);
                Map<String, Object> data = mapper.readValue(albumsJson, documentMapType);
                
                // Instructs the device to play the audio response defined in the specified document 
                RenderDocumentDirective renderDocumentDirective = RenderDocumentDirective.builder()
                        .withToken("AlbumListToken")
                        .withDocument(document)
                        .withDatasources(data)
                        .build();

                return input.getResponseBuilder()
                        .withSpeech(speechText)
                        .addDirective(renderDocumentDirective)
                        .build();

            } catch (IOException e) {
                throw new AskSdkException("Unable to read or deserialize the APL document", e);
            }
        } else {
            speechText = "My Images is designed for viewing images on a device with a screen, such as an Echo Show or Fire TV.";
        }

        // add the speech to a simple card response and return it for the case of a device w/out a screen.
        return responseBuilder
            .withSpeech(speechText)
            .build();
	}    	

}
