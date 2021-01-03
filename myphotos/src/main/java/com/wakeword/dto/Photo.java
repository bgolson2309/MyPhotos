package com.wakeword.dto;

public class Photo {
	private String cameraMake;
	private String cameraModel;
	private String focalLength;
	private String apertureFNumber;
	private String isoEquivalent;
	
	public Photo() {

	}
	public String getCameraMake() {
		return cameraMake;
	}
	public void setCameraMake(String cameraMake) {
		this.cameraMake = cameraMake;
	}
	public String getCameraModel() {
		return cameraModel;
	}
	public void setCameraModel(String cameraModel) {
		this.cameraModel = cameraModel;
	}
	public String getFocalLength() {
		return focalLength;
	}
	public void setFocalLength(String focalLength) {
		this.focalLength = focalLength;
	}
	public String getApertureFNumber() {
		return apertureFNumber;
	}
	public void setApertureFNumber(String apertureFNumber) {
		this.apertureFNumber = apertureFNumber;
	}
	public String getIsoEquivalent() {
		return isoEquivalent;
	}
	public void setIsoEquivalent(String isoEquivalent) {
		this.isoEquivalent = isoEquivalent;
	}
}
