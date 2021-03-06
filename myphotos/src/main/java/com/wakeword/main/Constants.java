package com.wakeword.main;

public class Constants {
	
    public static final String MIME_TYPE = "application/json";
    public static final String BUY = "Buy";
    public static final String UPSELL = "Upsell";
    public static final String CANCEL = "Cancel";
    public static final String REQUEST = "request";
    public static final String NAME = "name";
    public static final String PAYLOAD = "payload";
    public static final String PURCHASE_RESULT = "purchaseResult";
    public static final String STATUS = "status";
    public static final String CODE = "code";
    public static final String PRODUCT_ID = "productId";
    public static final String CONFIRMED_STATUS = "CONFIRMED";
    
	//messages
    public static final String WELCOME = "Welcome to Running Journal!  I can keep track of your running activity for you.";
    public static final String WHAT_DO_YOU_WANT =  "What do you want to ask?";

    public static final String NOTIFY_MISSING_PERMISSIONS = "Please enable email and full name permissions in the Amazon Alexa app under the Running Journal skill settings.  It's required to support any request of your running journal to be emailed to you.";
    public static final String ERROR= "Uh Oh. Looks like something went wrong.";
    public static final String API_FAILURE= "There was an error with the Alexa skill. Please try again.";
    public static final String GOODBYE= "Goodbye! Thanks for using My Images.";
    public static final String UNHANDLED= "This skill doesn't support that. Please ask something else.";
    public static final String STOP= "Bye! Thanks for using My Images.";
	public static final String HELP = "You can ask me to open a photo album or view images by saying next or previous.";
	public static final String HELP_REPROMPT = "Do you want to see recent photos?";
	public static final String MY_PHOTOS = "My Images";
	public static final String FIRST_VISIT = "Welcome to My Images! Do you want to view your most recent albums, or photos?";
	public static final String NO_RESPONSE = "Ok.  Perhaps another time.";
	public static final String HUH = "I didn't catch that. What can I help you with?";
	public static final String NO_ISP = "There are no in skill products to sell or refund to you right now.";
	public static final String WHAT_CAN_I_BUY = "Products available for purchase at this time is Premium Access.   Premium Access allows you full navigation, filtering images on categories, searching by time, and full screen image slideshow functionality. If you are ready to buy, say, 'Buy' premium access. So what can I help you with?";
	public static final String YOU_BOUGHT_PREMIUM = "You purchased Premium Access, and now you can enjoy rich image filtering, searching, viewing and navigation.";
	public static final String YOU_HAVENT_BOUGHT_PREMIUM = "Premium Access must be purchased for full navigation, filtering images on categories, searching by time, or full screen slideshow functionality.";
	public static final String CANCEL_ACCEPTED = "Even without Premium Access, you can continue to browse albums and images.";

	   
    // custom slots
    public static final String CATEGORY = "category";
    public static final String LIST_POSITION_SLOT = "ListPosition";
    public static final String IMAGE_POSITION_SLOT = "ImagePosition";
    public static final String DURATION_SLOT = "numberOf";
    
    //custom intents
    public static final String FULL_SCREEN_INTENT = "FullScreenIntent";
    public static final String LIST_ALBUMS_INTENT = "ListAlbumsIntent";
    public static final String SELECT_ALBUM = "SelectAlbumIntent";
    public static final String SELECT_IMAGE = "SelectImageIntent";
    public static final String LIST_IMAGES_INTENT = "ListImagesIntent";
    public static final String FILTER_IMAGES_INTENT = "FilterImagesIntent";
    public static final String IMAGES_FOR_DATE_INTENT = "ImagesForDateIntent"; 
    public static final String SLIDE_SHOW_INTENT = "SlideShowIntent";     
    public static final String AMAZON_YES_INTENT = "AMAZON.YesIntent";
    public static final String AMAZON_NO_INTENT = "AMAZON.NoIntent";
    public static final String AMAZON_NEXT_INTENT = "AMAZON.NextIntent";
    public static final String AMAZON_PREVIOUS_INTENT = "AMAZON.PreviousIntent";
    public static final String AMAZON_HELP_INTENT ="AMAZON.HelpIntent";
    public static final String AMAZON_STOP_INTENT ="AMAZON.StopIntent";
    public static final String AMAZON_CANCEL_INTENT ="AMAZON.CancelIntent";
    public static final String AMAZON_FALLBACK_INTENT ="AMAZON.FallbackIntent";
    public static final String WHAT_CAN_I_BUY_INTENT ="WhatCanIBuyIntent";
    public static final String PURCHASE_HISTORY_INTENT ="PurchaseHistoryIntent";
    public static final String BUY_PREMIUM_ACCESS_INTENT ="BuyPremiumAccessIntent";
    public static final String CANCEL_PREMIUM_ACCESS_INTENT ="CancelPremiumAccessIntent";

    
    //session keys or values
    public static final String PREV_QUESTION_CONTEXT = "Prev_Question_Context";

}
