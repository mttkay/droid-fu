package com.github.droidfu.cachefu;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class HttpResponseCache extends LIFOCache<String, byte[]> {

    public HttpResponseCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("WebServiceCache", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    @Override
    protected File getFileNameForKey(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected byte[] readValueFromDisk(File file) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected void writeValueToDisk(FileOutputStream ostream, byte[] responseBody)
            throws IOException {
        new BufferedOutputStream(ostream).write(responseBody);
    }
}
