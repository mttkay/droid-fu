package com.github.droidfu.cachefu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

public class HttpResponseCache extends AbstractCache<String, byte[]> {

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
