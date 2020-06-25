package com.github.youtube.downloader;

/*-
 * #
 * Java youtube video and audio downloader
 *
 * Copyright (C) 2020 Igor Kiulian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #
 */

import java.io.File;

import com.github.youtube.downloader.model.Status;

public interface OnYoutubeDownloadListener {

    void onDownloading(int progress);

    void onFinished(File file);

    void onError(Throwable throwable);
    
    Status getProgress();
    
    File getFile();
    
    void setBaseURl(String baseURl);
    
    String getBaseURl();
    
	String getFileName();

	void setFileName(String fileName);
	
	String getVid();
	
	void setVid(String vid);
	
	void setExt(String ext);
	
	String getExt();

}
