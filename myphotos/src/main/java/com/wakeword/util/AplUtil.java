package com.wakeword.util;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.SupportedInterfaces;
import com.wakeword.dto.Album;
import com.wakeword.dto.MediaItem;

public class AplUtil {

	public static String media_item_data = "{" + 
			"    \"imageTemplateData\": {" + 
			"        \"type\": \"object\"," + 
			"        \"objectId\": \"imageSample\"," + 
			"        \"properties\": {" + 
			"            \"backgroundImage\": {" + 
			"                \"contentDescription\": null," + 
			"                \"smallSourceUrl\": null," + 
			"                \"largeSourceUrl\": null," + 
			"                \"sources\": [" + 
			"                    {" + 
			"                        \"url\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/background_black.jpg\"," + 
			"                        \"size\": \"small\"," + 
			"                        \"widthPixels\": 0," + 
			"                        \"heightPixels\": 0" + 
			"                    }," + 
			"                    {" + 
			"                        \"url\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/background_black.jpg\"," + 
			"                        \"size\": \"large\"," + 
			"                        \"widthPixels\": 0," + 
			"                        \"heightPixels\": 0" + 
			"                    }" + 
			"                ]" + 
			"            }," + 
			"            \"image\": {" + 
			"                \"contentDescription\": \"{image-description}\"," + 
			"                \"smallSourceUrl\": null," + 
			"                \"largeSourceUrl\": null," + 
			"                \"sources\": [" + 
			"                    {" + 
			"                        \"url\": \"{baseURL}=w{width-px}-h{height-px}\"," + 
			"                        \"size\": \"small\"," + 
			"                        \"widthPixels\": 0," + 
			"                        \"heightPixels\": 0" + 
			"                    }," + 
			"                    {" + 
			"                        \"url\": \"{baseURL}=w{width-px}-h{height-px}\"," + 
			"                        \"size\": \"large\"," + 
			"                        \"widthPixels\": 0," + 
			"                        \"heightPixels\": 0" + 
			"                    }" + 
			"                ]" + 
			"            }," + 
			"            \"title\": \"{image-title}\"," + 
			"            \"logoUrl\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/logo-min6.png\"" + 
			"        }" + 
			"    }" + 
			"}";
	
	
	
	
	public static String album_list_top = "{" + 
			"    \"imageListData\": {" + 
			"        \"type\": \"object\"," + 
			"        \"objectId\": \"imageListSample\"," + 
			"        \"backgroundImage\": {" + 
			"            \"contentDescription\": null," + 
			"            \"smallSourceUrl\": null," + 
			"            \"largeSourceUrl\": null," + 
			"            \"sources\": [" + 
			"                {" + 
			"                    \"url\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/background_black.jpg\"," + 
			"                    \"size\": \"small\"," + 
			"                    \"widthPixels\": 0," + 
			"                    \"heightPixels\": 0" + 
			"                }," + 
			"                {" + 
			"                    \"url\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/background_black.jpg\"," + 
			"                    \"size\": \"large\"," + 
			"                    \"widthPixels\": 0," + 
			"                    \"heightPixels\": 0" + 
			"                }" + 
			"            ]" + 
			"        }," + 
			"        \"title\": \"Photo Albums:\"," + 
			"        \"listItems\": [";

	public static String album_template = "            {" + 
			"                \"primaryText\": \"{album-title}\"," + 
			"                \"secondaryText\": \"Photos: {image-count}\"," + 
			"                \"imageAlignment\": \"{right-left}\"," + 
			"                \"imageSource\": \"{album-cover-url}\"," + 
			"                \"primaryAction\": {" + 
			"				    \"type\": \"SendEvent\"," + 
			"				        \"arguments\": [" + 
			"					      \"AlbumListItemSelected\"," + 
			"					      \"{album-title}\"," + 
			"					      \"{album-uuid}\"" + 
			"					    ]," + 
			"					    \"components\": [ ]" + 
			"				  }" + 
			"            }";
	
	public static String comma = ","; 
	public static String album_bottom = "        ]," + 
			"        \"logoUrl\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/logo-min6.png\"," + 
			"        \"hintText\": \"Try, \\\"Alexa, ask My Photos to open first album.\\\"\"" + 
			"    }" + 
			"}";
	
	
	public static String photosTop = "{\r\n" + 
			"    \"imageListData\": {\r\n" + 
			"        \"type\": \"object\",\r\n" + 
			"        \"objectId\": \"imageList\",\r\n" + 
			"        \"backgroundImage\": {\r\n" + 
			"            \"contentDescription\": null,\r\n" + 
			"            \"smallSourceUrl\": null,\r\n" + 
			"            \"largeSourceUrl\": null,\r\n" + 
			"            \"sources\": [\r\n" + 
			"                {\r\n" + 
			"                    \"url\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/background_black.jpg\",\r\n" + 
			"                    \"size\": \"small\",\r\n" + 
			"                    \"widthPixels\": 0,\r\n" + 
			"                    \"heightPixels\": 0\r\n" + 
			"                },\r\n" + 
			"                {\r\n" + 
			"                    \"url\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/background_black.jpg\",\r\n" + 
			"                    \"size\": \"large\",\r\n" + 
			"                    \"widthPixels\": 0,\r\n" + 
			"                    \"heightPixels\": 0\r\n" + 
			"                }\r\n" + 
			"            ]\r\n" + 
			"        },\r\n" + 
			"        \"title\": \"Photo Album: {album-title}\",\r\n" + 
			"        \"listItems\": [";
			
	public static String photos_template = "            {\r\n" + 
			"                \"primaryText\": \"{image-name}\",\r\n" + 
			"                \"imageAlignment\": \"{right-left}\",\r\n" + 
			"                \"imageSource\": \"{baseURL}=w{width-px}-h{height-px}\",\r\n" + 
			"                \"primaryAction\": {\r\n" + 
			"				    \"type\": \"SendEvent\",\r\n" + 
			"				        \"arguments\": [\r\n" + 
			"					      \"ImageListItemSelected\",\r\n" + 
			"					      \"{image-uuid}\"\r\n" + 
			"					    ]\r\n" + 
			"				  }\r\n" + 
			"            }";
	
	public static String photos_bottom = "        ],\r\n" + 
			"        \"logoUrl\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/logo-min6.png\",\r\n" + 
			"        \"hintText\": \"Try, \\\"Alexa, ask My Photos to begin slideshow.\\\"\"\r\n" + 
			"    }\r\n" + 
			"}";
			
	public static String buildAlbumData(Album[] albums) {
		String jsonAlbumData = album_list_top + album_template;
		
		for (int i = 0; i < albums.length; i++) {
			if (i == 0) {
				jsonAlbumData=jsonAlbumData.replace("{right-left}","right");
			} else {
				jsonAlbumData = jsonAlbumData + comma;
				jsonAlbumData = jsonAlbumData + album_template;
				jsonAlbumData=jsonAlbumData.replace("{right-left}","left");
			}
			jsonAlbumData=jsonAlbumData.replace("{album-title}",albums[i].getTitle());
			jsonAlbumData=jsonAlbumData.replace("{image-count}",albums[i].getMediaItemsCount());
			jsonAlbumData=jsonAlbumData.replace("{album-cover-url}",albums[i].getCoverPhotoBaseUrl());
			jsonAlbumData=jsonAlbumData.replace("{album-uuid}",albums[i].getId());
			jsonAlbumData=jsonAlbumData.replace("{album-title}",albums[i].getTitle());
		}
		
		jsonAlbumData = jsonAlbumData + album_bottom;
		return jsonAlbumData;
	}
	
	public static String buildPhotoData(MediaItem[] media, int currentPixelWidth, int currentPixelHeight, String albumTitle) {
		String jsonPhotosData = photosTop + photos_template;
		jsonPhotosData = jsonPhotosData.replace("{album-title}",albumTitle);
		
		for (int i = 0; i < media.length; i++) {
			if (i == 0) {
				jsonPhotosData=jsonPhotosData.replace("{right-left}","right");
			} else {
				jsonPhotosData = jsonPhotosData + comma;
				jsonPhotosData = jsonPhotosData + photos_template;
				jsonPhotosData=jsonPhotosData.replace("{right-left}","left");
			}
			String formattedDateTime = media[i].getMediaMetadata().convertToReadableFormat(media[i].getMediaMetadata().getCreationTime());
			jsonPhotosData=jsonPhotosData.replace("{image-name}", formattedDateTime);
			jsonPhotosData=jsonPhotosData.replace("{baseURL}", media[i].getBaseUrl());
			jsonPhotosData=jsonPhotosData.replace("{width-px}", String.valueOf(currentPixelWidth));
			jsonPhotosData=jsonPhotosData.replace("{height-px}", String.valueOf(currentPixelHeight));
			jsonPhotosData=jsonPhotosData.replace("{image-uuid}",media[i].getId());
		}
		
		jsonPhotosData = jsonPhotosData + photos_bottom;
		return jsonPhotosData;
	}
	
	public static String buildSelectedMediaData(MediaItem media, int currentPixelWidth, int currentPixelHeight) {
		
		String jsonMediasData = media_item_data;
		
		String formattedDateTime = media.getMediaMetadata().convertToReadableFormat(media.getMediaMetadata().getCreationTime());
		jsonMediasData=jsonMediasData.replace("{image-title}", formattedDateTime);
		jsonMediasData=jsonMediasData.replace("{image-description}", media.getFilename());
		jsonMediasData=jsonMediasData.replace("{baseURL}", media.getBaseUrl());
		jsonMediasData=jsonMediasData.replace("{baseURL}", media.getBaseUrl());
		jsonMediasData=jsonMediasData.replace("{width-px}", String.valueOf(currentPixelWidth));
		jsonMediasData=jsonMediasData.replace("{width-px}", String.valueOf(currentPixelWidth));
		jsonMediasData=jsonMediasData.replace("{height-px}", String.valueOf(currentPixelHeight));
		jsonMediasData=jsonMediasData.replace("{height-px}", String.valueOf(currentPixelHeight));
		 System.out.println("MEDIA JSON = " + jsonMediasData);	
		
		return jsonMediasData;
	}
	
    public static boolean supportsApl(HandlerInput input) {
        SupportedInterfaces supportedInterfaces = input.getRequestEnvelope().getContext().getSystem().getDevice().getSupportedInterfaces();
        return supportedInterfaces.getAlexaPresentationAPL() != null;
    }
    
}
