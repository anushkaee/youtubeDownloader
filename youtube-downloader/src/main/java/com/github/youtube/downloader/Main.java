package com.github.youtube.downloader;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Future;

import com.github.youtube.downloader.cipher.CipherFunction;
import com.github.youtube.downloader.model.Status;
import com.github.youtube.downloader.model.VideoDetails;
import com.github.youtube.downloader.model.YoutubeVideo;
import com.github.youtube.downloader.model.formats.AudioFormat;
import com.github.youtube.downloader.model.formats.AudioVideoFormat;
import com.github.youtube.downloader.model.formats.Format;
import com.github.youtube.downloader.model.formats.VideoFormat;
import com.github.youtube.downloader.model.quality.AudioQuality;
import com.github.youtube.downloader.model.quality.VideoQuality;
import com.github.youtube.downloader.parser.DefaultParser;
import com.github.youtube.downloader.parser.Parser;

public class Main {

	public static void main2(String[] args) throws YoutubeException, IOException {

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
		String videoId = "O5HQ1sZseKg"; // for url https://www.youtube.com/watch?v=abc12345
		YoutubeVideo video = downloader.getVideo(videoId);

		// video details
		VideoDetails details = video.details();
		System.out.println(details.title());

		System.out.println(details.viewCount());
		details.thumbnails().forEach(image -> System.out.println("Thumbnail: " + image));

		// get videos with audio
		List<AudioVideoFormat> videoWithAudioFormats = video.videoWithAudioFormats();
		videoWithAudioFormats.forEach(it -> {
		    System.out.println(it.videoQuality() + " : " + it.url());
		});

		
		
		  // filtering only video formats 
		 List<VideoFormat> videoFormats = video.findVideoWithQuality(VideoQuality.hd720);
		  videoFormats.forEach(it -> { System.out.println(it.videoQuality() + " : " + it.url()); });
		  
		  List<AudioFormat> videoFormats2 = video.findAudioWithQuality(AudioQuality.high);
		  
		  
		 
		 

		// itags can be found here - https://gist.github.com/sidneys/7095afe4da4ae58694d128b1034e01e2
		Format formatByItag = video.findFormatByItag(136); 
		if (formatByItag != null) {
		    System.out.println(formatByItag.url());
		}

		File outputDir = new File("/data/projects/test_projects/videos/my_videos");

		// sync downloading
		File file = video.download(videoWithAudioFormats.get(0), outputDir);

		// async downloading with callback
		video.downloadAsync(videoWithAudioFormats.get(0), outputDir, new OnYoutubeDownloadListener() {
		    @Override
		    public void onDownloading(int progress) {
		        System.out.printf("Downloaded %d%%\n", progress);
		    }
		            
		    @Override
		    public void onFinished(File file) {
		        System.out.println("Finished file: " + file);
		    }

		    @Override
		    public void onError(Throwable throwable) {
		        System.out.println("Error: " + throwable.getLocalizedMessage());
		    }

			@Override
			public Status getProgress() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public File getFile() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setBaseURl(String baseURl) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getBaseURl() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getFileName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setFileName(String fileName) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getVid() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void setVid(String vid) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setExt(String ext) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public String getExt() {
				// TODO Auto-generated method stub
				return null;
			}
		});

		/*
		 * // async downloading with future Future<File> future =
		 * video.downloadAsync(format, outputDir); File file = future.get(5,
		 * TimeUnit.SECONDS);
		 */

		// live videos and streams
		if (video.details().isLive()) {
		    System.out.println("Live Stream HLS URL: " + video.details().liveUrl());
		}

	}

}
