package com.github.droidfu.cachefu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

public class CachedList<CO extends CachedModel> extends CachedModel {

    protected Class<? extends CachedModel> clazz;
    protected List<CO> list;

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

    public CachedList(Class<? extends CachedModel> clazz, Parcel source) throws IOException {
        super(source);
        initList(clazz);
        list = new ArrayList<CO>();
    }

    public CachedList(Class<? extends CachedModel> clazz, String id) {
        super(id);
        initList(clazz);
        list = new ArrayList<CO>();
    }

    private void initList(Class<? extends CachedModel> clazz) {
        this.clazz = clazz;
    }

    public List<CO> getList() {
        return list;
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

        @SuppressWarnings("unchecked")
        @Override
        public CachedList<CachedModel> createFromParcel(Parcel source) {
            String className = source.readString();
            Class<? extends CachedModel> clazz;
            try {
                clazz = (Class<? extends CachedModel>) Class.forName(className);
                return new CachedList<CachedModel>(clazz, source);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                return null;
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

    @SuppressWarnings("unchecked")
    @Override
    public void readFromParcel(Parcel source) throws IOException {
        super.readFromParcel(source);
        list = source.readArrayList(clazz.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(clazz.getCanonicalName());
        dest.writeTypedList(list);
    }

}
