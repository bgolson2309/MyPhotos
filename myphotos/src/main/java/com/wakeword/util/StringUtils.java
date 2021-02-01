package com.wakeword.util;

import com.wakeword.dto.MediaItem;

public class StringUtils {

	
	public static String capitalizeAllWords(String str){  
	    String words[]=str.split("\\s");  
	    String capitalizeWord="";  
	    for(String w:words){  
	        String first=w.substring(0,1);  
	        String afterfirst=w.substring(1);  
	        capitalizeWord+=first.toUpperCase()+afterfirst+" ";  
	    }  
	    return capitalizeWord.trim();  
	} 
	
	public static String makeImageList(MediaItem[] media)
	{
		String imageList = null;
		for (int i = 0; i < media.length; i++)
		{
			imageList = imageList + media[i].getId() + ",";
		}
		
		return imageList;
	}
}
