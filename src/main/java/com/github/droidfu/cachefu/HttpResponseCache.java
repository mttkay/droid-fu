package com.github.droidfu.cachefu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class HttpResponseCache extends AbstractCache<String, byte[]> {

    public HttpResponseCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("HttpCache", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    @Override
    public String getFileNameForKey(String url) {
        // replace all special URI characters with a single + symbol
        return url.replaceAll("[.:/,%?&=]", "+").replaceAll("[+]+", "+");
    }

    @Override
    protected byte[] readValueFromDisk(File file) throws IOException {
        BufferedInputStream istream = new BufferedInputStream(new FileInputStream(file));
        long fileSize = file.length();
        if (fileSize > Integer.MAX_VALUE) {
            throw new IOException("Cannot read files larger than " + Integer.MAX_VALUE + " bytes");
        }

        byte[] data = new byte[(int) fileSize];
        istream.read(data, 0, (int) fileSize);
        istream.close();

        return data;
    }

    @Override
    protected void writeValueToDisk(BufferedOutputStream ostream, byte[] responseBody)
            throws IOException {
        ostream.write(responseBody);
    }
}
