package com.github.droidfu.cachefu;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Allows caching Model objects using the features provided by {@link AbstractCache}. The key into
 * the cache will be based around the cached object's key, and the object will be able to save and
 * reload itself from the cache.
 * 
 * @author Michael England
 * 
 */
public class ModelCache extends AbstractCache<String, CachedObject> {

    public ModelCache(int initialCapacity, long expirationInMinutes, int maxConcurrentThreads) {
        super("ModelCache", initialCapacity, expirationInMinutes, maxConcurrentThreads);
    }

    public synchronized void removeAllWithPrefix(String keyPrefix) {
        Set<String> keys = keySet();

        for (String key : keys) {
            if (key.startsWith(keyPrefix)) {
                remove(key);
            }
        }

        if (isDiskCacheEnabled()) {
            removeExpiredCache(keyPrefix);
        }
    }

    private void removeExpiredCache(final String keyPrefix) {
        final File cacheDir = new File(diskCacheDirectory);

        if (!cacheDir.exists()) {
            return;
        }

        File[] list = cacheDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return dir.equals(cacheDir) && filename.startsWith(getFileNameForKey(keyPrefix));
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
    protected CachedObject readValueFromDisk(File file) throws IOException {
        FileInputStream istream = new FileInputStream(file);

        byte[] dataWritten = new byte[(int) file.length()];
        BufferedInputStream bistream = new BufferedInputStream(istream);
        bistream.read(dataWritten);
        bistream.close();

        Parcel parcelIn = Parcel.obtain();
        parcelIn.unmarshall(dataWritten, 0, dataWritten.length);
        parcelIn.setDataPosition(0);
        DescribedCachedObject result = new DescribedCachedObject();
        result.readFromParcel(parcelIn);

        return result.getCachedObject();
    }

    @Override
    protected void writeValueToDisk(File file, CachedObject data) throws IOException {
        DescribedCachedObject describedCachedObject = new DescribedCachedObject();
        describedCachedObject.setCachedObject(data);

        Parcel parcelOut = Parcel.obtain();
        describedCachedObject.writeToParcel(parcelOut, 0);
        byte[] dataWritten = parcelOut.marshall();

        FileOutputStream ostream = new FileOutputStream(file);
        BufferedOutputStream bistream = new BufferedOutputStream(ostream);
        bistream.write(dataWritten);
    }

    static class DescribedCachedObject implements Parcelable {

        private CachedObject cachedObject;

        public void setCachedObject(CachedObject cachedObject) {
            this.cachedObject = cachedObject;
        }

        public CachedObject getCachedObject() {
            return cachedObject;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(cachedObject.getClass().getCanonicalName());
            dest.writeParcelable(cachedObject, flags);
            cachedObject.writeToParcel(dest, flags);
        }

        public void readFromParcel(Parcel source) throws IOException {
            String className = source.readString();
            Class<?> clazz;
            try {
                clazz = Class.forName(className);
                cachedObject = source.readParcelable(clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IOException(e.getMessage());
            }
        }

    }

}
