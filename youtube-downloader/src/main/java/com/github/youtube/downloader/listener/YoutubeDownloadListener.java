package com.github.youtube.downloader.listener;

import java.io.File;

import com.github.youtube.downloader.OnYoutubeDownloadListener;
import com.github.youtube.downloader.model.Status;

public class YoutubeDownloadListener implements OnYoutubeDownloadListener {

	private int progress;
	private File file;
	private String baseURl;
	private String fileName;
	private String vid;
	private String ext;

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public void setProgress(int progress) {
		this.progress = progress;
	}

	public void setFile(File file) {
		this.file = file;
	}

	private Status status =new Status();
	
	
	@Override
	public void onDownloading(int progress) {
		this.progress = progress;
		
	}

	@Override
	public void onFinished(File file) {
		this.file = file;
		status.setTitle(fileName);
		status.setDownloadLink(baseURl+"/download/"+vid);
		
	}

	@Override
	public void onError(Throwable throwable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Status getProgress() {
		status.setStatus(progress);
		return status;
	}

	@Override
	public File getFile() {		
		return file;
	}
	
	public String getBaseURl() {
		return baseURl;
	}

	public void setBaseURl(String baseURl) {
		this.baseURl = baseURl;
	}
	
	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	@Override
	public void setExt(String ext) {
		this.ext = ext;
		
	}

	@Override
	public String getExt() {
		// TODO Auto-generated method stub
		return this.ext;
	}

}
