package com.github.droidfu.imageloader;

import java.util.LinkedHashMap;

import android.graphics.Bitmap;

@SuppressWarnings("serial")
public class ImageCache extends LinkedHashMap<String, Bitmap> {

    private static final int MAX_CACHE_SIZE = 10;

    @Override
    protected boolean removeEldestEntry(
            java.util.Map.Entry<String, Bitmap> eldest) {
        return size() > MAX_CACHE_SIZE;
    }

}
