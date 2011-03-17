package com.github.droidfu.cachefu;

import java.io.IOException;

import android.os.Parcel;
import android.os.Parcelable;

public abstract class CachedModel implements Parcelable {

    private String id;
    private long transactionId = Long.MIN_VALUE;

    public CachedModel() {
    }

    public CachedModel(Parcel source) throws IOException {
        readFromParcel(source);
    }

    public CachedModel(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public String getKey() {
        if (id == null) {
            return null;
        } else {
            return createKey(id);
        }
    }

    public static CachedModel find(ModelCache modelCache, String id,
            Class<? extends CachedModel> clazz) {
        CachedModel testObject;
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
            CachedModel cachedModel = modelCache.get(key);
            if ((cachedModel != null) && (cachedModel.transactionId > this.transactionId)) {
                reloadFromCachedModel(modelCache, cachedModel);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public abstract String createKey(String id);

    public abstract boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel);

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    @SuppressWarnings("unused")
    public void readFromParcel(Parcel source) throws IOException {
        id = source.readString();
    }

}
