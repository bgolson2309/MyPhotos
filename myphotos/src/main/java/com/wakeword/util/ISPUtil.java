package com.wakeword.util;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.interfaces.connections.SendRequestDirective;
import com.amazon.ask.model.services.monetization.InSkillProduct;
import com.amazon.ask.model.services.monetization.InSkillProductsResponse;
import com.amazon.ask.model.services.monetization.MonetizationServiceClient;
import com.amazon.ask.request.RequestHelper;
import com.wakeword.main.Constants;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ISPUtil {

    public static Optional<InSkillProduct> getInSkillProduct(List<InSkillProduct> inSkillProducts, String productId) {
        for(InSkillProduct inSkillProduct : inSkillProducts) {
            if(inSkillProduct.getProductId().equalsIgnoreCase(productId)) {
                return Optional.of(inSkillProduct);
            }
        }
        return Optional.empty();
    }

    public static String getRandomObject(String[] strings) {
        final int index = new Random().nextInt(strings.length);
        return strings[index];
    }

    public static String getBuyResponseText(String referenceName, String productName) {
    	if (referenceName.equalsIgnoreCase("Premium_Access")) {
            return String.format("Now that you have %s, you can ask for running statistics, and customize the name of your favorite running team!", productName);
        } else {
        	return "Sorry, that's not a valid product";
        }
        
    }
    
    public static boolean isEntitled(InSkillProduct product) {
        return null!=product && product.getEntitled().toString().equalsIgnoreCase("ENTITLED");
    }

    public static SendRequestDirective getDirectiveByType(String productId, String type) {
        // Prepare the directive payload
        Map<String, Object> payload = new HashMap<>();
        Map<String, Object> inskillProduct = new HashMap<>();
        inskillProduct.put("productId", productId);
        payload.put("InSkillProduct", inskillProduct);

        // Prepare the directive request
        SendRequestDirective directive = SendRequestDirective.builder()
                .withPayload(payload)
                .withName(type)
                .withToken("correlationToken")
                .build();

        return directive;
    }

    /**
     * Gets the upsell SendRequestDirective
     * @param productId
     * @param upsellMessage
     * @return SendRequestDirective
     * */
    private static SendRequestDirective getUpsellDirective(String productId, String upsellMessage) {

        // Prepare the directive payload
        Map<String,Object> payload = new HashMap<>();
        Map<String, Object> inskillProduct = new HashMap<>();
        inskillProduct.put("productId", productId);
        payload.put("upsellMessage", upsellMessage);
        payload.put("InSkillProduct", inskillProduct);

        return SendRequestDirective.builder()
                .withPayload(payload)
                .withName("Upsell")
                .withToken("correlationToken")
                .build();
    }

    /**
     * Gets a list of available products
     * */
    public static List<String> getListOfAvailableProducts(List<InSkillProduct> inSkillProducts) {
        return inSkillProducts.stream()
                .filter(product -> product.getEntitled().toString().equalsIgnoreCase("NOT_ENTITLED")
                        && product.getPurchasable().toString().equalsIgnoreCase("PURCHASABLE"))
                .map(product -> product.getName())
                .collect(Collectors.toList());
    }

    /**
     * Gets a list of entitled products
     * */
    public static List<String> getAllEntitledProducts(List<InSkillProduct> inSkillProducts) {
        return inSkillProducts.stream()
                .filter(product -> product.getEntitled().toString().equalsIgnoreCase("ENTITLED"))
                .map(product -> product.getName())
                .collect(Collectors.toList());
    }

    public static Optional<InSkillProduct> getPremiumAccessProduct(List<InSkillProduct> products) {
        for(InSkillProduct product : products) {
            if(product.getReferenceName().equalsIgnoreCase("Premium_Access")) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }

    public static String getSpeakableListOfProducts(List<String> inSkillProducts) {
        String s = inSkillProducts.toString();
        String productListSpeech = s.substring(1, s.length() - 1).replace(", ", ",");
        return productListSpeech.replaceFirst(",([^,]+)$", " and$1");
    }

    /**
     * Checks if upsell should be made
     */
    public static boolean shouldUpsell(HandlerInput input) {
        if (null == input.getRequestEnvelopeJson().get("request").get("intent")) {
            //If the last intent was Connections.Response, do not up-sell
            return false;
        }
        boolean[] options = new boolean[]{true, false};
        Random random = new Random();
        int index = random.nextInt(options.length);
        return options[index];
    }
    
    public static Optional<InSkillProduct> getPremiumSubscriptionProduct(List<InSkillProduct> products) {
        for(InSkillProduct product : products) {
            if(product.getReferenceName().equalsIgnoreCase("Premium_Access")) {
                return Optional.of(product);
            }
        }
        return Optional.empty();
    }
    
    public static Optional<Response> getResponseBasedOnAccessType(HandlerInput input, List<InSkillProduct> products, String preSpeechText) {
        final Optional<InSkillProduct> premiumSubscriptionProduct = getPremiumSubscriptionProduct(products);

        String speechText;
        if(premiumSubscriptionProduct.isPresent()) {
            if(isEntitled(premiumSubscriptionProduct.get())) {
                //Customer has bought Premium Access. 
                speechText = "Congratulations on purchasing Premium Access!  Now you can ask me for your running statistics, or ask me to customize your favorite running team name to personalize my response.";
            } else {
                //Customer has NOT bought Premium Access
            	speechText = "Purchasing Premium Access is a great way to get your running statistics, or customize your favorite running team name to personalize my response.  Just say, purchase premium access.";
            }
        } else {
            speechText = String.format("Sorry, no in-skill products found.");
        }
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .build();
    }
    
    public static boolean hasPremiumAccess(HandlerInput handlerInput) {
        final RequestHelper requestHelper = RequestHelper.forHandlerInput(handlerInput);
        final String locale = requestHelper.getLocale();
        final MonetizationServiceClient client = handlerInput.getServiceClientFactory().getMonetizationService();
        final InSkillProductsResponse response = client.getInSkillProducts(locale, null, null, null, null, null);
        final List<InSkillProduct> inSkillProducts = response.getInSkillProducts();
        
        final List<String> entitledProducts = ISPUtil.getAllEntitledProducts(inSkillProducts);  //Get what's been purchased
        
        if (entitledProducts.size() > 0) {
        	return true;
        } else {
        	return false;
        }
    }

    
}
