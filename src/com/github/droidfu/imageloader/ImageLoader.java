package com.github.droidfu.imageloader;

import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

/**
 * Realizes an background image loader backed by a FIFO cache. If the image to
 * be loaded is present in the cache, it is set immediately on the given view.
 * Otherwise, a thread from a thread pool will be used to download the image in
 * the background and set the image on the view as soon as it completes.
 * 
 * @author Matthias Kaeppler
 */
public class ImageLoader implements Runnable {

    private static final int NUM_THREADS = 2;

    private static final ExecutorService executor;

    private static final ImageCache imageCache;

    static final int HANDLER_MESSAGE_ID = 0;

    static final String BITMAP_EXTRA = "droidfu:extra_bitmap";

    static {
        executor = Executors.newFixedThreadPool(NUM_THREADS);
        imageCache = new ImageCache();
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

    public void run() {
        try {
            URL url = new URL(imageUrl);
            Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
            synchronized (imageCache) {
                imageCache.put(imageUrl, bitmap);
            }
            notifyImageLoaded(bitmap);
        } catch (Throwable e) {
            // do not bother the user with failed picture downloads
            e.printStackTrace();
        }
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

    public void notifyImageLoaded(Bitmap bitmap) {
        Message message = new Message();
        message.what = HANDLER_MESSAGE_ID;
        Bundle data = new Bundle();
        data.putParcelable(BITMAP_EXTRA, bitmap);
        message.setData(data);

        handler.sendMessage(message);
    }
}
