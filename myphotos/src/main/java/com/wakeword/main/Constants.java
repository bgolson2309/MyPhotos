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
    public static final String GOODBYE= "Goodbye! Thanks for using My Photos.";
    public static final String UNHANDLED= "This skill doesn't support that. Please ask something else.";
    public static final String STOP= "Bye! Thanks for using Running Journal to track your running activity.";
	public static final String HELP = "You can ask me to open a photo album or view images by saying next or previous.";
	public static final String HELP_REPROMPT = "Do you want to see recent photos?";
	public static final String MY_PHOTOS = "My Photos";
	public static final String FIRST_VISIT = "Welcome to My Photos! Do you want to view your most recent albums, or photos?";
	public static final String NO_RESPONSE = "Ok.  Perhaps another time.";
	
	//S3 bucket for persistence of reading list by Alexa account ID
	public static final String BUCKET = "wakeword.skill.myphotos"; 

    //profile permissions
    public static final String NAME_PERMISSION = "alexa::profile:name:read";
    public static final String EMAIL_PERMISSION = "alexa::profile:email:read";
    public static final String MOBILE_NUMBER_PERMISSION = "alexa::profile:mobile_number:read";
    
    // custom slots
    public static final String BRAND_SLOT = "brand";
    public static final String TEAM_SLOT = "team";
    public static final String KILOMETER_SLOT = "kilometer";
    public static final String DURATION_SLOT = "duration";
    public static final String DATE_SLOT = "date";
    public static final String FELT_SLOT = "felt";
    public static final String DENIED_SLOT_STATUS = "DENIED";
    
    //custom intents
    public static final String EMAIL_JOURNAL_INTENT = "EmailJournalIntent";
    public static final String LIST_ALBUMS_INTENT = "ListAlbumsIntent";
    public static final String LIST_IMAGES_INTENT = "ListImagesIntent";
    public static final String SET_FAVORITE_TEAM_INTENT = "SetFavoriteTeamIntent";
    public static final String LOG_RUN_INTENT = "LogRunIntent";
    public static final String DISPLAY_RUNS_INTENT = "DisplayRunsIntent";
    public static final String DISPLAY_STATISTICS_INTENT = "ShowStatisticsIntent";
    public static final String BEST_TEAM_INTENT = "BestTeamIntent";
    public static final String AMAZON_YES_INTENT = "AMAZON.YesIntent";
    public static final String AMAZON_NO_INTENT = "AMAZON.NoIntent";
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
    public static final String PREV_QUESTION_ADD_SHOES = "Prev_Question_Add_Shoes";
    public static final String PREV_QUESTION_ADD_RUN = "Prev_Question_Add_Run";
    public static final String PREV_QUESTION_UPSELL = "Prev_Question_Upsell";
    public static final String PREV_QUESTION_CUSTOMIZE_TEAM = "Prev_Question_Customize_Team";
    public static final String PREV_QUESTION_SHOW_STATS = "Prev_Question_Show_Stats";
}
