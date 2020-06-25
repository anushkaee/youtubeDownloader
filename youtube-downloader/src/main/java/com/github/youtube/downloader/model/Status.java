package com.github.youtube.downloader.model;

import org.springframework.beans.factory.annotation.Value;

public class Status {
	private String status;
	private String downloadLink="NOT_READY";
	private String title;
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	@Value("${baseUrl}")
	private String baseUrl;
	
	public void setStatus(int progrss) {
		this.status = progrss+"%";
	}

	public String getProgress() {
		return status;
	}
	
	public String getDownloadLink() {
		return downloadLink;
	}

	public void setDownloadLink(String downloadLink) {
		this.downloadLink = downloadLink;
	}

}
