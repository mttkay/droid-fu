/* Copyright (c) 2009 Matthias Kaeppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.droidfu.cachefu;

import java.io.BufferedOutputStream;
import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;

/**
 * <p>
 * A simple 2-level cache for bitmap images consisting of a small and fast
 * in-memory cache (1st level cache) and a slower but bigger disk cache (2nd
 * level cache). For second level caching, the application's cache directory
 * will be used. Please note that Android may at any point decide to wipe that
 * directory.
 * </p>
 * <p>
 * When pulling from the cache, it will first attempt to load the image from
 * memory. If that fails, it will try to load it from disk. If that succeeds,
 * the image will be put in the 1st level cache and returned. Otherwise it's a
 * cache miss, and the caller is responsible for loading the image from
 * elsewhere (probably the Internet).
 * </p>
 * <p>
 * Pushes to the cache are always write-through (i.e., the image will be stored
 * both on disk and in memory).
 * </p>
 * 
 * @author Matthias Kaeppler
 */
public class ImageCache extends LIFOCache<String, Bitmap> {

    private int cachedImageQuality = 75;

    private CompressFormat compressedImageFormat = CompressFormat.PNG;

    public ImageCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("WebImages", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    /**
     * The image format that should be used when caching images on disk. The
     * default value is {@link CompressFormat#PNG}. Note that when switching to
     * a format like JPEG, you will lose any transparency that was part of the
     * image.
     * 
     * @param compressedImageFormat
     *        the {@link CompressFormat}
     */
    public void setCompressedImageFormat(CompressFormat compressedImageFormat) {
        this.compressedImageFormat = compressedImageFormat;
    }

    public CompressFormat getCompressedImageFormat() {
        return compressedImageFormat;
    }

    /**
     * @param cachedImageQuality
     *        the quality of images being compressed and written to disk (2nd
     *        level cache) as a number in [0..100]
     */
    public void setCachedImageQuality(int cachedImageQuality) {
        this.cachedImageQuality = cachedImageQuality;
    }

    public int getCachedImageQuality() {
        return cachedImageQuality;
    }

    @Override
    public String getFileNameForKey(String imageUrl) {
        // TODO: is hashCode appropriate to avoid collisions?
        // the key is the image URL
        return Integer.toHexString(imageUrl.hashCode()) + "." + compressedImageFormat.name();
    }

    @Override
    protected Bitmap readValueFromDisk(File file) {
        return BitmapFactory.decodeFile(file.getAbsolutePath());
    }

    @Override
    protected void writeValueToDisk(BufferedOutputStream ostream, Bitmap value) {
        value.compress(compressedImageFormat, cachedImageQuality, ostream);
    }
}
