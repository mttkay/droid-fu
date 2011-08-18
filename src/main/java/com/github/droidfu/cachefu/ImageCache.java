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

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;

import java.io.*;

/**
 * Implements a cache capable of caching image files. It exposes helper methods to immediately
 * access binary image data as {@link Bitmap} objects.
 * 
 * @author Matthias Kaeppler
 * 
 */
public class ImageCache extends AbstractCache<String, byte[]> {

    private MemoryInfo mi;
    private ActivityManager activityManager;
    private File path;
    private StatFs stat;
    private Context mContext;

    public ImageCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("ImageCache", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    public synchronized void removeAllWithPrefix(String urlPrefix) {
        CacheHelper.removeAllWithStringPrefix(this, urlPrefix);
    }

    @Override
    public String getFileNameForKey(String imageUrl) {
        return CacheHelper.getFileNameFromUrl(imageUrl);
    }

    @Override
    protected byte[] readValueFromDisk(File file) throws IOException {
        BufferedInputStream istream = new BufferedInputStream(new FileInputStream(file));
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("Cannot read files larger than " + Integer.MAX_VALUE + " bytes");
        }

        int imageDataLength = (int) fileSize;

        byte[] imageData = new byte[imageDataLength];
        istream.read(imageData, 0, imageDataLength);
        istream.close();

        return imageData;
    }

    public synchronized Bitmap getBitmap(Object elementKey) {
        byte[] imageData = super.get(elementKey);
        if (imageData == null) {
            return null;
        }
        return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
    }

    @Override
    protected void writeValueToDisk(File file, byte[] imageData) throws IOException {
        switch(getDiskCacheStatus()){
            case DISK_CACHE_INTERNAL:

                activityManager.getMemoryInfo(mi);

                if(mi.availMem > imageData.length){
                    BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(file));
                    ostream.write(imageData);

                    ostream.close();
                } else{
                    enableDiskCache(mContext, DISK_CACHE_SDCARD);
                }

                break;

            case DISK_CACHE_SDCARD:

                 long blockSize = stat.getBlockSize();
                 long availableBlocks = stat.getAvailableBlocks();
                 Long mFreenSD = (availableBlocks * blockSize);

                if(mFreenSD > imageData.length){
                    BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(file));
                    ostream.write(imageData);

                    ostream.close();
                } else{
                    enableDiskCache(mContext, DISK_CACHE_INTERNAL);
                }

                break;
        }


    }

    @Override
    /**
     * Enable caching to the phone's internal storage or SD card.
     *
     * @param context
     *            the current context
     * @param storageDevice
     *            where to store the cached files, either {@link #DISK_CACHE_INTERNAL} or
     *            {@link #DISK_CACHE_SDCARD})
     * @return
     */
    public boolean enableDiskCache(Context context, int storageDevice) {

        mContext = context;

        mi = new MemoryInfo();
        activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);

        path = Environment.getExternalStorageDirectory();
        stat = new StatFs(path.getPath());


        return super.enableDiskCache(context, storageDevice);
    }
}
