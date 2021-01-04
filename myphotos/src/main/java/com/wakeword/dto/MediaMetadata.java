package com.wakeword.dto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MediaMetadata {
	public MediaMetadata() {
		// TODO Auto-generated constructor stub
	}
	private String creationTime;
	private String width;
	private String height;
	private Photo photo;
	
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
		//this.creationTime = convertToReadableFormat(creationTime);
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public Photo getPhoto() {
		return photo;
	}
	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
	public static String convertToReadableFormat(String dateStr) {
		String readableDate = null;
		try {
		    TimeZone utc = TimeZone.getTimeZone("UTC");
		    SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		    SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    sourceFormat.setTimeZone(utc);
		    Date convertedDate = sourceFormat.parse(dateStr);
		    readableDate = destFormat.format(convertedDate);
		} catch (Exception e) {
			System.out.println(e);
		}
		return readableDate;
	}
	
}
