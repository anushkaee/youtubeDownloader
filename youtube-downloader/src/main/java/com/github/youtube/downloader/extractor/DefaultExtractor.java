package com.github.youtube.downloader.extractor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.youtube.downloader.YoutubeException;

public class DefaultExtractor implements Extractor {
    private static final Pattern YT_PLAYER_CONFIG = Pattern.compile(";ytplayer\\.config = (\\{.*?\\});");

    private static final String DEFAULT_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";
    private static final String DEFAULT_ACCEPT_LANG = "en-US,en;";
    private static final int DEFAULT_RETRY_ON_FAILURE = 3;

    private Map<String, String> requestProperties = new HashMap<>();
    private int retryOnFailure = DEFAULT_RETRY_ON_FAILURE;

    public DefaultExtractor() {
        setRequestProperty("User-Agent", DEFAULT_USER_AGENT);
        setRequestProperty("Accept-language", DEFAULT_ACCEPT_LANG);
    }

    @Override
    public void setRequestProperty(String key, String value) {
        requestProperties.put(key, value);
    }

    @Override
    public void setRetryOnFailure(int retryOnFailure) {
        if (retryOnFailure < 0)
            throw new IllegalArgumentException("retry count should be > 0");
        this.retryOnFailure = retryOnFailure;
    }

    @Override
    public String extractYtPlayerConfig(String html) throws YoutubeException {
        Matcher matcher = YT_PLAYER_CONFIG.matcher(html);

        if (matcher.find()) {
            return matcher.group(1);
        }

        throw new YoutubeException.BadPageException("Could not parse web page");
    }

    @Override
    public String loadUrl(String url) throws YoutubeException {
        int retryCount = retryOnFailure;
        while (retryCount >= 0) {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                for (Map.Entry<String, String> entry : requestProperties.entrySet()) {
                    connection.setRequestProperty(entry.getKey(), entry.getValue());
                }

                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream(), StandardCharsets.UTF_8));

                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    sb.append(inputLine).append('\n');
                in.close();
                return sb.toString();
            } catch (IOException e) {
                retryCount--;
            }
        }
        throw new YoutubeException.VideoUnavailableException("Could not load url: " + url);
    }
}
