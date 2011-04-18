package com.github.droidfu.cachefu;

import java.io.IOException;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class CachedList<CO extends CachedModel> extends CachedModel {

    protected Class<? extends CachedModel> clazz;
    protected ArrayList<CO> list;

    public CachedList() {
        list = new ArrayList<CO>();
    }

    public CachedList(Class<? extends CachedModel> clazz) {
        initList(clazz);
        list = new ArrayList<CO>();
    }

    public CachedList(Class<? extends CachedModel> clazz, int initialLength) {
        initList(clazz);
        list = new ArrayList<CO>(initialLength);
    }

    public CachedList(Parcel source) throws IOException {
        super(source);
    }

    public CachedList(Class<? extends CachedModel> clazz, String id) {
        super(id);
        initList(clazz);
        list = new ArrayList<CO>();
    }

    private void initList(Class<? extends CachedModel> clazz) {
        this.clazz = clazz;
    }

    public synchronized ArrayList<CO> getList() {
        return new ArrayList<CO>(list);
    }

    public synchronized void add(CO cachedObject) {
        list.add(cachedObject);
    }

    public synchronized void set(int index, CO cachedObject) {
        list.set(index, cachedObject);
    }

    public synchronized CO get(int index) {
        return list.get(index);
    }

    public synchronized int size() {
        return list.size();
    }

    @Override
    public synchronized boolean equals(Object o) {
        if (!(o instanceof CachedList)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        CachedList that = (CachedList) o;
        return clazz.equals(that.clazz) && list.equals(that.list);
    }

    @Override
    public synchronized String createKey(String id) {
        return "list_" + id;
    }

    @Override
    public synchronized boolean reload(ModelCache modelCache) {
        boolean result = reload(modelCache);
        return result;
    }

    @Override
    public synchronized boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel) {
        boolean internalObjectReloaded = false;
        @SuppressWarnings("unchecked")
        CachedList<CO> cachedList = (CachedList<CO>) cachedModel;
        clazz = cachedList.clazz;
        list = cachedList.list;
        for (CachedModel listModel : list) {
            if (listModel.reload(modelCache)) {
                internalObjectReloaded = true;
            }
        }
        return internalObjectReloaded;
    }

    public static final Creator<CachedList<CachedModel>> CREATOR = new Parcelable.Creator<CachedList<CachedModel>>() {

        @SuppressWarnings({ "rawtypes", "unchecked" })
        @Override
        public CachedList<CachedModel> createFromParcel(Parcel source) {
            try {
                return new CachedList(source);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @SuppressWarnings("unchecked")
        @Override
        public CachedList<CachedModel>[] newArray(int size) {
            return new CachedList[size];
        }

    };

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void readFromParcel(Parcel source) throws IOException {
        super.readFromParcel(source);
        String className = source.readString();
        try {
            clazz = (Class<? extends CachedModel>) Class.forName(className);
            list = source.createTypedArrayList((Creator) clazz.getField("CREATOR").get(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(clazz.getCanonicalName());
        dest.writeTypedList(list);
    }

}
