package com.github.youtube.downloader.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.youtube.downloader.OnYoutubeDownloadListener;
import com.github.youtube.downloader.YoutubeException;
import com.github.youtube.downloader.model.InitStatus;
import com.github.youtube.downloader.model.Status;
import com.github.youtube.downloader.service.DownloadService;
import com.github.youtube.downloader.service.FileTypes;

@Controller
@CrossOrigin(origins = "${client.url}")
public class DownloadController {

	@Autowired
	private DownloadService downloadService;

	@RequestMapping(path = "/download/{vid}", method = RequestMethod.GET)
	public ResponseEntity<InputStreamResource> download(@PathVariable String vid) throws IOException {

		OnYoutubeDownloadListener listener = downloadService.getFile(vid);
		File file = listener.getFile();
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		String fileName = listener.getFileName()+"."+listener.getExt();
		fileName = URLEncoder.encode(fileName, "UTF-8").replace('+', '_');;
		
		HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename="+fileName);

		return ResponseEntity.ok()
				.headers(headers)
				.contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(resource);
	}

	@RequestMapping(path = "/initdownload/{fileType}/{vid}", method = RequestMethod.POST)
	public ResponseEntity initDownload(@PathVariable FileTypes fileType,
			@PathVariable("vid") String vid) throws IOException, YoutubeException {
		InitStatus initStatus = null;
		if (FileTypes.MP4.equals(fileType)) {
			initStatus = downloadService.downloadMp4(vid);
		} else if (FileTypes.MP3.equals(fileType)) {
			initStatus = downloadService.downloadMp3(vid);
		}
		 
		ResponseEntity responseEntity = new ResponseEntity(initStatus,HttpStatus.CREATED);

		return responseEntity;

	}
	
	@RequestMapping(path = "/status/{vid}", method = RequestMethod.GET)
	@ResponseBody
	public Status getStatus(@PathVariable("vid") String vid) throws IOException, YoutubeException {
		
		return downloadService.getProgress(vid);

	}

}
