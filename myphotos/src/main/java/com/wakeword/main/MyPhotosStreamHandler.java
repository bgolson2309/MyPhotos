package com.wakeword.main;

import com.amazon.ask.Skill;
import com.amazon.ask.SkillStreamHandler;
import com.amazon.ask.Skills;
import com.wakeword.handlers.*;
import com.wakeword.interceptors.*;

public class MyPhotosStreamHandler extends SkillStreamHandler {

    @SuppressWarnings("unchecked")
	private static Skill getSkill() {
        return Skills.standard()
                .addRequestHandlers(
                        new CancelandStopIntentHandler(),
                        new HelpIntentHandler(),
                        new NoIntentHandler(),
                        new FallbackIntentHandler(),
                        new LaunchRequestHandler(),
                        new SessionEndedRequestHandler(),
                        new UnhandledIntentHandler())
                .withSkillId("amzn1.ask.skill.02fcabf1-98e3-4102-8c97-c98808a5b3e0")
                .addExceptionHandler(new ServiceExceptionHandler())
                .addExceptionHandler(new GenericExceptionHandler())
                .addRequestInterceptor(new RequestLogInterceptor())
                .addResponseInterceptor(new ResponseLogInterceptor())
                .build();
    }
    public MyPhotosStreamHandler() {
        super(getSkill());
    }
}