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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.github.droidfu.support.StringSupport;
import com.google.common.collect.MapMaker;

/**
 * <p>
 * A simple 2-level LIFO cache consisting of a small and fast in-memory cache
 * (1st level cache) and a slower but bigger disk cache (2nd level cache). For
 * disk caching, either the application's cache directory or the SD card can be
 * used. Please note that in the case of the app cache dir, Android may at any
 * point decide to wipe that entire directory if it runs low on internal
 * storage. The SD card cache is managed solely by this class instead.
 * </p>
 * <p>
 * When pulling from the cache, it will first attempt to load the data from
 * memory. If that fails, it will try to load it from disk. If that succeeds,
 * the data will be put in the in-memory cache and returned (read-through).
 * Otherwise it's a cache miss, and the caller is responsible for loading the
 * image from elsewhere (probably the Internet).
 * </p>
 * <p>
 * Pushes to the cache are always write-through (i.e., the image will be stored
 * both on disk and in memory).
 * </p>
 * 
 * @author Matthias Kaeppler
 */
public abstract class LIFOCache<KeyT, ValT> implements Map<KeyT, ValT> {

    public static final int DISK_CACHE_INTERNAL = 0;
    public static final int DISK_CACHE_SDCARD = 1;

    private static final String LOG_TAG = "Droid-Fu[CacheFu]";

    private boolean shouldCacheToDisk;

    private String diskCacheDirectory;

    private ConcurrentMap<KeyT, ValT> cache;

    private String name;

    public LIFOCache(String name, int initialCapacity, long expirationInMinutes,
            int maxConcurrentThreads) {

        this.name = name;

        MapMaker mapMaker = new MapMaker();
        mapMaker.initialCapacity(initialCapacity);
        mapMaker.expiration(expirationInMinutes * 60, TimeUnit.SECONDS);
        mapMaker.concurrencyLevel(maxConcurrentThreads);
        mapMaker.weakValues();
        this.cache = mapMaker.makeMap();
    }

    public boolean enableDiskCache(Context context, int storageDevice) {
        Context appContext = context.getApplicationContext();

        String rootDir = null;
        // SD-card available
        if (storageDevice == DISK_CACHE_SDCARD
                && Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            rootDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            appContext.getCacheDir().getAbsolutePath();
        }

        this.diskCacheDirectory = rootDir + "/cachefu/"
                + StringSupport.underscore(name.replaceAll("\\s", ""));
        shouldCacheToDisk = new File(diskCacheDirectory).mkdirs();

        if (!shouldCacheToDisk) {
            Log.w(LOG_TAG, "Failed creating disk cache directory " + diskCacheDirectory);
        }

        return shouldCacheToDisk;
    }

    public String getDiskCacheDirectory() {
        return diskCacheDirectory;
    }

    protected abstract File getFileNameForKey(KeyT key);

    protected abstract ValT readValueFromDisk(File file);

    protected abstract void writeValueToDisk(FileOutputStream ostream, ValT value)
            throws IOException;

    protected void cacheToDisk(KeyT key, ValT value) {
        File file = getFileNameForKey(key);
        try {
            file.createNewFile();

            FileOutputStream ostream = new FileOutputStream(file);

            writeValueToDisk(ostream, value);

            ostream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public synchronized ValT get(Object elementKey) {
        KeyT key = (KeyT) elementKey;
        ValT value = cache.get(key);

        if (value != null) {
            // memory hit
            return value;
        }

        // memory miss, try reading from disk
        File file = getFileNameForKey(key);
        if (file.exists()) {
            // disk hit
            value = readValueFromDisk(file);
            if (value == null) {
                // treat decoding errors as a cache miss
                return null;
            }
            cache.put(key, value);
            return value;
        }

        // cache miss
        return null;
    }

    public ValT put(KeyT key, ValT value) {
        if (shouldCacheToDisk) {
            cacheToDisk(key, value);
        }

        return cache.put(key, value);
    }

    public void putAll(Map<? extends KeyT, ? extends ValT> t) {
        throw new UnsupportedOperationException();
    }

    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return cache.containsValue(value);
    }

    public ValT remove(Object key) {
        return cache.remove(key);
    }

    public Set<KeyT> keySet() {
        return cache.keySet();
    }

    public Set<Map.Entry<KeyT, ValT>> entrySet() {
        return cache.entrySet();
    }

    public int size() {
        return cache.size();
    }

    public boolean isEmpty() {
        return cache.isEmpty();
    }

    public void clear() {
        cache.clear();
    }

    public Collection<ValT> values() {
        return cache.values();
    }
}
