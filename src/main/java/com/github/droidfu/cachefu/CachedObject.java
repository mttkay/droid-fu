package com.github.droidfu.cachefu;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class CachedObject implements Parcelable {

    private String id;

    public CachedObject() {
    }

    public CachedObject(Parcel source) {
        readFromParcel(source);
    }

    public CachedObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        if (id == null) {
            return null;
        } else {
            return createKey(id);
        }
    }

    public static CachedObject find(ModelCache modelCache, String id,
            Class<? extends CachedObject> clazz) {
        CachedObject testObject;
        try {
            testObject = clazz.newInstance();
        } catch (Exception e) {
            return null;
        }
        testObject.setId(id);
        if (testObject.reload(modelCache)) {
            return testObject;
        } else {
            return null;
        }
    }

    public boolean save(ModelCache modelCache) {
        return save(modelCache, getKey());
    }

    protected boolean save(ModelCache modelCache, String saveKey) {
        if ((modelCache != null) && (saveKey != null)) {
            modelCache.put(saveKey, this);
            return true;
        } else {
            return false;
        }
    }

    public boolean reload(ModelCache modelCache) {
        String key = getKey();
        if ((modelCache != null) && (key != null)) {
            CachedObject cachedObject = modelCache.get(key);
            if (cachedObject != null) {
                reloadFromCachedObject(modelCache, cachedObject);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public abstract String createKey(String id);

    public abstract void reloadFromCachedObject(ModelCache modelCache, CachedObject cachedObject);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    public void readFromParcel(Parcel source) {
        id = source.readString();
    }

}
