package com.github.droidfu.imageloader;

import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

/**
 * Realizes an background image loader backed by a two-level FIFO cache. If the
 * image to be loaded is present in the cache, it is set immediately on the
 * given view. Otherwise, a thread from a thread pool will be used to download
 * the image in the background and set the image on the view as soon as it
 * completes.
 * 
 * @author Matthias Kaeppler
 */
public class ImageLoader implements Runnable {

    private static final ThreadPoolExecutor executor;

    private static final ImageCache imageCache;

    private static final int DEFAULT_POOL_SIZE = 2;

    static final int HANDLER_MESSAGE_ID = 0;

    static final String BITMAP_EXTRA = "droidfu:extra_bitmap";

    private static int numAttempts = 3;

    static {
        executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEFAULT_POOL_SIZE);
        imageCache = new ImageCache();
    }

    /**
     * @param numThreads
     *        the maximum number of threads that will be started to download
     *        images in parallel
     */
    public static void setThreadPoolSize(int numThreads) {
        executor.setMaximumPoolSize(numThreads);
    }

    /**
     * @param numAttempts
     *        how often the image loader should retry the image download if
     *        network connection fails
     */
    public static void setMaxDownloadAttempts(int numAttempts) {
        ImageLoader.numAttempts = numAttempts;
    }

    /**
     * @param count
     *        how many images loaded from the web should be cached in memory,
     *        i.e. in the 1st level cache. Be careful not to set this too high,
     *        otherwise you will meet the dreaded {@link OutOfMemoryError}.
     */
    public static void setMaximumNumberOfImagesInMemory(int count) {
        ImageCache.firstLevelCacheSize = count;
    }

    /**
     * @return how many images loaded from the web should be cached in memory,
     *         i.e. in the 1st level cache. Be careful not to set this too high,
     *         otherwise you will meet the dreaded {@link OutOfMemoryError}.
     */
    public static int getMaximumNumberOfImagesInMemory() {
        return ImageCache.firstLevelCacheSize;
    }

    public static void initialize(Context context) {
        ImageCache.initialize(context);
    }

    private String imageUrl;

    private Handler handler;

    private ImageLoader(String imageUrl, ImageView imageView) {
        this.imageUrl = imageUrl;
        this.handler = new ImageLoaderHandler(imageView);
    }

    private ImageLoader(String imageUrl, ImageLoaderHandler handler) {
        this.imageUrl = imageUrl;
        this.handler = handler;
    }

    public static void start(String imageUrl, ImageView imageView) {
        ImageLoader loader = new ImageLoader(imageUrl, imageView);
        synchronized (imageCache) {
            Bitmap image = imageCache.get(imageUrl);
            if (image == null) {
                // fetch the image in the background
                executor.execute(loader);
            } else {
                imageView.setImageBitmap(image);
            }
        }
    }

    public static void start(String imageUrl, ImageLoaderHandler handler) {
        ImageLoader loader = new ImageLoader(imageUrl, handler);
        synchronized (imageCache) {
            Bitmap image = imageCache.get(imageUrl);
            if (image == null) {
                // fetch the image in the background
                executor.execute(loader);
            } else {
                loader.notifyImageLoaded(image);
            }
        }
    }

    /**
     * Clears the 1st-level cache (in-memory cache). A good candidate for
     * calling in {@link Application#onLowMemory()}.
     */
    public static void clearCache() {
        imageCache.clear();
    }

    public void run() {
        Bitmap bitmap = null;
        int timesTried = 1;

        while (timesTried <= numAttempts) {
            try {
                URL url = new URL(imageUrl);
                bitmap = BitmapFactory.decodeStream(url.openStream());
                synchronized (imageCache) {
                    imageCache.put(imageUrl, bitmap);
                }
                break;
            } catch (Throwable e) {
                Log.w(ImageLoader.class.getSimpleName(), "download for " + imageUrl
                        + " failed (attempt " + timesTried + ")");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e1) {
                }

                timesTried++;
            }
        }

        if (bitmap != null) {
            notifyImageLoaded(bitmap);
        }
    }

    public void notifyImageLoaded(Bitmap bitmap) {
        Message message = new Message();
        message.what = HANDLER_MESSAGE_ID;
        Bundle data = new Bundle();
        data.putParcelable(BITMAP_EXTRA, bitmap);
        message.setData(data);

        handler.sendMessage(message);
    }
}
