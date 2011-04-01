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

    public ArrayList<CO> getList() {
        return list;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CachedList)) {
            return false;
        }
        @SuppressWarnings("rawtypes")
        CachedList that = (CachedList) o;
        return clazz.equals(that.clazz) && list.equals(that.list);
    }

    @Override
    public String createKey(String id) {
        return "list_" + id;
    }

    public boolean reloadAll(ModelCache modelCache) {
        boolean result = reload(modelCache);
        for (CachedModel cachedModel : list) {
            if (cachedModel.reload(modelCache)) {
                result = true;
            }
        }
        return result;
    }

    @Override
    public boolean reloadFromCachedModel(ModelCache modelCache, CachedModel cachedModel) {
        @SuppressWarnings("unchecked")
        CachedList<CO> cachedList = (CachedList<CO>) cachedModel;
        clazz = cachedList.clazz;
        list = cachedList.list;
        return false;
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
