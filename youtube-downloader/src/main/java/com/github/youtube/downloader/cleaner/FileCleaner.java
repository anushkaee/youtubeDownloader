package com.github.youtube.downloader.cleaner;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.github.youtube.downloader.listener.YoutubeDownloadListener;
import com.github.youtube.downloader.service.DownloadService;

@Service
public class FileCleaner implements Runnable {

	@Value("${storageLocation}")
	private String storageLoction;

	@Value("${deleteJobFrequecncyInMinutes}")
	private int deleteJobFrequencyInMinutes;

	@Value("${deleteFileOlderThanInMinutes}")
	private int deleteFilesOderThanInMinutes;
	
	@Autowired
	private DownloadService downloadService;
	
	private static Map<Long, String> deleteQue = new HashMap<>();
	private static List<Long> deletedTimeStamps =new ArrayList<>();

	public void addToDeleteQue(long time, String vid) {
		deleteQue.put(time, vid);
	}

	@PostConstruct
	public void scheduleJob() {
		System.out.println("Scheduling Deletion job...");

		/*
		 * Calendar due = Calendar.getInstance(); due.set(Calendar.MILLISECOND, 0);
		 * due.set(Calendar.SECOND, 0); due.set(Calendar.MINUTE, 20); if
		 * (due.before(Calendar.getInstance())) { due.add(Calendar.HOUR, 1); }
		 */
		//long milliSecondsToNextOcurrence = due.getTimeInMillis() - new Date().getTime();
		long milliSecondsToNextOcurrence = 60000;
		final ScheduledExecutorService s = Executors.newSingleThreadScheduledExecutor();

		//FileCleaner task = new FileCleaner();
		System.out.println("Deletion Job Frequency:"+deleteJobFrequencyInMinutes+" minutes");
		s.scheduleAtFixedRate(this, milliSecondsToNextOcurrence, deleteJobFrequencyInMinutes * 60 * 1000,
				TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {

		deleteFilesOlderThanNdays(deleteFilesOderThanInMinutes, storageLoction);
		deleteInternalQue(deleteFilesOderThanInMinutes);
	}

	public void deleteFilesOlderThanNdays(int minutesBack, String dirWay) {
		System.out.println("Deleting files in location ["+dirWay+"]"+" older than: "+minutesBack+" minutes");
		File directory = new File(dirWay);
		if (directory.exists()) {

			File[] listFiles = directory.listFiles();
			long purgeTime = System.currentTimeMillis() - (minutesBack * 60 * 1000);
			for (File listFile : listFiles) {
				if (listFile.lastModified() < purgeTime) {
					if (!listFile.delete()) {
						System.err.println("Unable to delete file: " + listFile);
					} else {
						System.out.println("Deleted: "+listFile.getName());
					}
				}
			}
		}
	}
	
	public void deleteInternalQue(int minutesBack) {
		Map<String,YoutubeDownloadListener> fileMap = downloadService.getFileMap();
		deleteQue.forEach((k,v) -> {
			
			long purgeTime = System.currentTimeMillis() - (minutesBack * 60 * 1000);
			if (k < purgeTime) {
				if(fileMap.containsKey(v)) {
					fileMap.remove(v);
					System.out.println("Removing video from internal Queue: "+v);
					deletedTimeStamps.add(k);
				}
			}		
		});
		clearDeleteQueue();		
	}
	
	private void clearDeleteQueue() {
	
		deletedTimeStamps.forEach( t -> {
			if(deleteQue.containsKey(t)) {
				deleteQue.remove(t);
				System.out.println("Removing video from delete Queue: "+t);
			}
		});
		
		deletedTimeStamps = new ArrayList<Long>();
	}
}
