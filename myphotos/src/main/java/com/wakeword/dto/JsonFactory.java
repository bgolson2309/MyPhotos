package com.wakeword.dto;

import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.ObjectMapperImpl;

import java.util.List;


public class JsonFactory {


    private static ObjectMapper json = JsonFactory.create();

    public static ObjectMapper create () {
        JsonParserFactory jsonParserFactory = new JsonParserFactory();
        jsonParserFactory.lax();

        return new ObjectMapperImpl(jsonParserFactory,  new JsonSerializerFactory());
    }

    public static String toJson(Object value) {
         return json.toJson( value );
    }

    public static void toJson(Object value, Appendable appendable) {
         json.toJson( value, appendable );
    }

    public static <T> T fromJson(String str, Class<T> clazz) {
        return json.fromJson(str, clazz);
    }


    public static <T> List<T> fromJsonArray(String str, Class<T> clazz) {
        return json.parser().parseList(clazz, str);
    }

    public static ObjectMapper create (JsonParserFactory parserFactory, JsonSerializerFactory serializerFactory) {
        return new ObjectMapperImpl(parserFactory, serializerFactory);
    }

    public static ObjectMapper createUseProperties (boolean useJsonDates) {
        JsonParserFactory jpf = new JsonParserFactory();
        jpf.usePropertiesFirst();
        JsonSerializerFactory jsf = new JsonSerializerFactory();

        jsf.usePropertiesFirst();

        if (useJsonDates) {
            jsf.useJsonFormatForDates();
        }
        return new ObjectMapperImpl(jpf, jsf);
    }

    public static ObjectMapper createUseAnnotations (boolean useJsonDates) {
        JsonParserFactory jpf = new JsonParserFactory();
        JsonSerializerFactory jsf = new JsonSerializerFactory();

        jsf.useAnnotations();

        if (useJsonDates) {
            jsf.useJsonFormatForDates();
        }
        return new ObjectMapperImpl(jpf, jsf);
    }


    public static ObjectMapper createUseJSONDates () {
        JsonParserFactory jpf = new JsonParserFactory();
        JsonSerializerFactory jsf = new JsonSerializerFactory();
        jsf.useJsonFormatForDates();
        return new ObjectMapperImpl(jpf, jsf);
    }

    public static String niceJson(String str) {
        return str.replace('\'', '\"');
    }
}