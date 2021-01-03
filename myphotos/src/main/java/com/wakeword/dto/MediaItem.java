package com.wakeword.dto;

public class MediaItem {
	public MediaItem() {

	}
	private String id;
	private String productUrl;
	private String baseUrl;
	private String mimeType;
	private MediaMetadata mediaMetadata;
	private String filename;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public String getBaseUrl() {
		return baseUrl;
	}
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	public MediaMetadata getMediaMetadata() {
		return mediaMetadata;
	}
	public void setMediaMetadata(MediaMetadata mediaMetadata) {
		this.mediaMetadata = mediaMetadata;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
}
