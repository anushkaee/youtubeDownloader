package com.github.youtube.downloader.cipher;

import com.github.youtube.downloader.YoutubeException;

public interface CipherFactory {

    Cipher createCipher(String jsUrl) throws YoutubeException;

    void addInitialFunctionPattern(int priority, String regex);

    void addFunctionEquivalent(String regex, CipherFunction function);
}
