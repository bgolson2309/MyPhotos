package com.wakeword.dto;

public class Video {
	private String cameraMake;
	private String cameraModel;
	private String fps;
	private String status;
	
	public Video() {

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
	public String getFps() {
		return fps;
	}
	public void setFps(String fps) {
		this.fps = fps;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}
