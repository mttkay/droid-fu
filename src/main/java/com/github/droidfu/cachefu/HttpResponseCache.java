package com.github.droidfu.cachefu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

import com.github.droidfu.http.CachedHttpResponse.ResponseData;

/**
 * Allows caching HTTP responses (only status code and payload at the moment) using the features
 * provided by {@link AbstractCache}. The key into the cache will be the request URL used to
 * retrieve the HTTP response in the first place.
 * 
 * @author Matthias Kaeppler
 * 
 */
public class HttpResponseCache extends AbstractCache<String, ResponseData> {

    public HttpResponseCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("HttpCache", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    public synchronized void removeAllWithPrefix(String urlPrefix) {
        Set<String> keys = keySet();

        for (String key : keys) {
            if (key.startsWith(urlPrefix)) {
                remove(key);
            }
        }
        
        if (isDiskCacheEnabled()) {
            removeExpiredCache(urlPrefix);
        }
    }

    private void removeExpiredCache(final String urlPrefix) {
        final File cacheDir = new File(diskCacheDirectory);
        
        if (!cacheDir.exists()) {
            return;
        }
        
        File[] list = cacheDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return dir.equals(cacheDir) && filename.startsWith(getFileNameForKey(urlPrefix));
            }
        });

        if (list == null || list.length == 0) {
            return;
        }
        
        for (File file : list) {
            file.delete();
        }
    }

    @Override
    public String getFileNameForKey(String url) {
        return CacheHelper.getFileNameFromUrl(url);
    }

    @Override
    protected ResponseData readValueFromDisk(File file) throws IOException {
        BufferedInputStream istream = new BufferedInputStream(new FileInputStream(file));
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("Cannot read files larger than " + Integer.MAX_VALUE + " bytes");
        }

        // first byte is the status code
        int statusCode = istream.read();

        // the remainder is the response data
        int responseDataLength = (int) fileSize - 1;

        byte[] responseBody = new byte[responseDataLength];
        istream.read(responseBody, 0, responseDataLength);
        istream.close();

        return new ResponseData(statusCode, responseBody);
    }

    @Override
    protected void writeValueToDisk(BufferedOutputStream ostream, ResponseData data)
            throws IOException {
        ostream.write(data.getStatusCode());
        ostream.write(data.getResponseBody());
    }
}
