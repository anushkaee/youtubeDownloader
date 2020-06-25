package com.github.youtube.downloader.extractor;

import com.github.youtube.downloader.YoutubeException;


public interface Extractor {

    void setRequestProperty(String key, String value);

    void setRetryOnFailure(int retryOnFailure);

    String extractYtPlayerConfig(String html) throws YoutubeException;

    String loadUrl(String url) throws YoutubeException;

}
