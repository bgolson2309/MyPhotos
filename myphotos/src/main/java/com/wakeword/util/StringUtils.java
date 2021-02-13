package com.wakeword.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wakeword.dto.Album;
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
		String imageList = "";
		for (int i = 0; i < media.length; i++)
		{
			imageList = imageList + media[i].getId() + ",";
		}
		
		return imageList;
	}
	
	public static String makeAlbumList(Album[] album)
	{
		String albumList = "";
		for (int i = 0; i < album.length; i++)
		{
			albumList = albumList + album[i].getId() + ",";
		}
		
		return albumList;
	}
	
	public static String getNextImageUUID(String btnPressed, String imageList, String currentImgUUID) {
    	String imageUUID = null;
    	String[] imageIdArray = imageList.split(",");
    	List<String> fixedLenghtList = Arrays.asList(imageIdArray); 
    	ArrayList<String> listOfID = new ArrayList<String>(fixedLenghtList);
    	int currentImgPos = listOfID.indexOf(currentImgUUID);
    	if (btnPressed.equals("PrevButton")) {
    		if ( currentImgPos == 0) {
    			imageUUID = listOfID.get(listOfID.size() -1); // go to end of the list 
    		} else {
    			imageUUID = listOfID.get(currentImgPos - 1); // go back one
    		}
    	} else {
    		if (currentImgPos == (listOfID.size() -1)) {
    			imageUUID = listOfID.get(0); // go to start of list
    		} else {
        		imageUUID = listOfID.get(currentImgPos + 1); // go forward one
    		}
    	}
    	
    	return imageUUID;
    }
	
	public static String getSelectedAlbumUUID(int numRequested, String albumList) {

    	String[] albumIdArray = albumList.split(",");
    	List<String> fixedLenghtList = Arrays.asList(albumIdArray); 
    	ArrayList<String> listOfID = new ArrayList<String>(fixedLenghtList);
    	return listOfID.get(numRequested - 1); 

    }
}
