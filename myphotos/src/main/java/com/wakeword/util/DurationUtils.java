package com.wakeword.util;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.wakeword.dto.Album;
import com.wakeword.dto.MediaItem;

public class DurationUtils {

	
	public static String revisedDays(String duration){  
	    String revisedDuration = null;
	    
	    switch (duration) {
        case "P1W":
        	revisedDuration = "P7D";
            break;
        case "P2W":
        	revisedDuration = "P14D";
            break;
        case "P3W":
        	revisedDuration = "P21D";
            break;
        case "P4W":
        	revisedDuration = "P28D";
            break;
        case "P5W":
        	revisedDuration = "P35D";
            break;
        case "P6W":
        	revisedDuration = "P42D";
            break;
        case "P7W":
        	revisedDuration = "P49D";
            break;
        case "P8W":
        	revisedDuration = "P56D";
            break;
        case "P9W":
        	revisedDuration = "P63D";
            break;
        case "P10W":
        	revisedDuration = "P70D";
            break;
        case "P11W":
        	revisedDuration = "P77D";
            break;
        case "P12W":
        	revisedDuration = "P84D";
            break;
        case "P1M":
        	revisedDuration = "P30D";
            break;
        case "P2M":
        	revisedDuration = "P60D";
            break;
        case "P3M":
        	revisedDuration = "P90D";
            break;
        case "P4M":
        	revisedDuration = "P120D";
            break;
        case "P5M":
        	revisedDuration = "P150D";
            break;
        case "P6M":
        	revisedDuration = "P180D";
            break;
            
            
        default:
        	revisedDuration = duration;
	    }

	    return revisedDuration;
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
	
	public static String makeAlbumTitleList(Album[] album)
	{
		String albumList = "";
		for (int i = 0; i < album.length; i++)
		{
			albumList = albumList + album[i].getTitle() + ",";
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
