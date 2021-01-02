package com.wakeword.util;

import com.wakeword.dto.Album;

public class AplUtil {

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
			"					      \"{album-uuid}\"" + 
			"					    ]," + 
			"					    \"components\": [ ]" + 
			"				  }" + 
			"            }";
	
	public static String album_seperator = ","; 
	public static String album_bottom = "        ]," + 
			"        \"logoUrl\": \"https://s3-us-west-2.amazonaws.com/wakeword.skill.myphotos/icon_108_A2Z.png\"," + 
			"        \"hintText\": \"Try, \\\"Alexa, ask My Photos to open first album.\\\"\"" + 
			"    }" + 
			"}";
	public static String buildAlbumData(Album[] albums) {
		String jsonAlbumData = album_list_top + album_template;
		
		for (int i = 0; i < albums.length; i++) {
			if (i == 0) {
				jsonAlbumData=jsonAlbumData.replace("{right-left}","right");
			} else {
				jsonAlbumData = jsonAlbumData + album_seperator;
				jsonAlbumData = jsonAlbumData + album_template;
				jsonAlbumData=jsonAlbumData.replace("{right-left}","left");
			}
			jsonAlbumData=jsonAlbumData.replace("{album-title}",albums[i].getTitle());
			jsonAlbumData=jsonAlbumData.replace("{image-count}",albums[i].getMediaItemsCount());
			jsonAlbumData=jsonAlbumData.replace("{album-cover-url}",albums[i].getCoverPhotoBaseUrl());
			jsonAlbumData=jsonAlbumData.replace("{album-uuid}",albums[i].getId());
			
		}
		
		jsonAlbumData = jsonAlbumData + album_bottom;
		return jsonAlbumData;
	}
}
