package com.github.youtube.downloader.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.youtube.downloader.OnYoutubeDownloadListener;
import com.github.youtube.downloader.YoutubeDownloader;
import com.github.youtube.downloader.YoutubeException;
import com.github.youtube.downloader.listener.YoutubeDownloadListener;
import com.github.youtube.downloader.model.InitStatus;
import com.github.youtube.downloader.model.Status;
import com.github.youtube.downloader.model.VideoDetails;
import com.github.youtube.downloader.model.YoutubeVideo;
import com.github.youtube.downloader.model.formats.AudioVideoFormat;
import com.github.youtube.downloader.model.formats.Format;
import com.github.youtube.downloader.model.quality.VideoQuality;
import com.github.youtube.downloader.parser.DefaultParser;

@Service
public class DownloadService {
	
	private static Map<String,YoutubeDownloadListener> fileMap = new HashMap<>(); 
	
	@Value("${baseUrl}")
	private String baseUrl;
	
	@Value("${storageLocation}")
	private String storageLoction;
	
	public InitStatus downloadMp4(String youtubeVideoId) throws YoutubeException, IOException {

		// you can easly implement or extend default parsing logic 
		YoutubeDownloader downloader = new YoutubeDownloader(new DefaultParser()); 
		// or just extend functionality via existing API
		// cipher features
		downloader.addCipherFunctionPattern(2, "\\b([a-zA-Z0-9$]{2})\\s*=\\s*function\\(\\s*a\\s*\\)\\s*\\{\\s*a\\s*=\\s*a\\.split\\(\\s*\"\"\\s*\\)");
	//	downloader.addCipherFunctionEquivalent("some regex for js function", new ReverseFunction());
		// extractor features
		downloader.setParserRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36");
		downloader.setParserRetryOnFailure(1);

		// parsing data
		YoutubeVideo video = downloader.getVideo(youtubeVideoId);

		// video details
		VideoDetails details = video.details();
		System.out.println(details.title());

		System.out.println(details.viewCount());
		details.thumbnails().forEach(image -> System.out.println("Thumbnail: " + image));

		// get videos with audio
		List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
		

		// itags can be found here - https://gist.github.com/sidneys/7095afe4da4ae58694d128b1034e01e2
		Format formatByItag = video.findFormatByItag(136); 
		if (formatByItag != null) {
		    System.out.println(formatByItag.url());
		}

		File outputDir = new File(storageLoction);

		// async downloading with callback
		YoutubeDownloadListener youtubeDownloadListener = new YoutubeDownloadListener();
		youtubeDownloadListener.setFileName(video.details().title());
		youtubeDownloadListener.setVid(youtubeVideoId);
		youtubeDownloadListener.setExt("mp4");
		youtubeDownloadListener.setBaseURl(baseUrl);
		AudioVideoFormat audioVideoFormat = videoWithAudioFormats.get(0);
		audioVideoFormat.setVideoQuality(VideoQuality.hd720);
		video.downloadAsync(audioVideoFormat, outputDir,youtubeDownloadListener);
		fileMap.put(youtubeVideoId, youtubeDownloadListener);


		// live videos and streams
		if (video.details().isLive()) {
		    System.out.println("Live Stream HLS URL: " + video.details().liveUrl());
		}
		
		InitStatus initStatus = new InitStatus();
		initStatus.setStatus("Download_Initiated");
		initStatus.setCheckProgress(baseUrl+"/status/"+youtubeVideoId);
		return initStatus;
	}
	
	public OnYoutubeDownloadListener getFile(String vid) {
		return fileMap.get(vid);
	}
	
	public Status getProgress(String vid) {
		return fileMap.get(vid).getProgress();
	}

}
