package com.wakeword.dto;

public class Album {
	private String id;
	private String title;
	private String productUrl;
	private String mediaItemsCount;
	private String coverPhotoBaseUrl;
	private String coverPhotoMediaItemId;
	
	public Album(String id, String title, String productUrl, String mediaItemsCount, String coverPhotoBaseUrl, String coverPhotoMediaItemId) {
		this();
		this.id = id;
		this.title = title;
		this.productUrl = productUrl;
		this.mediaItemsCount = mediaItemsCount;
		this.coverPhotoBaseUrl = coverPhotoBaseUrl;
		this.coverPhotoMediaItemId = coverPhotoMediaItemId;
	}
	public Album() {
		// TODO Auto-generated constructor stub
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public String getMediaItemsCount() {
		return mediaItemsCount;
	}
	public void setMediaItemsCount(String mediaItemsCount) {
		this.mediaItemsCount = mediaItemsCount;
	}
	public String getCoverPhotoBaseUrl() {
		return coverPhotoBaseUrl;
	}
	public void setCoverPhotoBaseUrl(String coverPhotoBaseUrl) {
		this.coverPhotoBaseUrl = coverPhotoBaseUrl;
	}
	public String getCoverPhotoMediaItemId() {
		return coverPhotoMediaItemId;
	}
	public void setCoverPhotoMediaItemId(String coverPhotoMediaItemId) {
		this.coverPhotoMediaItemId = coverPhotoMediaItemId;
	}
	
}
