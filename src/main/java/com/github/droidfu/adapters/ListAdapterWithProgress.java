/* Copyright (c) 2009 Matthias Käppler
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.droidfu.adapters;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

public abstract class ListAdapterWithProgress<T> extends BaseAdapter {

    private boolean isLoadingData;

    private View progressView;

    protected List<T> data = new ArrayList<T>();

    protected Activity activity;

    protected ListView listView;

    protected LayoutInflater inflater;

    public ListAdapterWithProgress(ListActivity activity, int progressDrawableResourceId) {
        this(activity, activity.getListView(), progressDrawableResourceId);
    }

    public ListAdapterWithProgress(Activity activity, ListView listView,
            int progressDrawableResourceId) {
        this.activity = activity;
        this.listView = listView;
        this.progressView = activity.getLayoutInflater().inflate(progressDrawableResourceId,
            listView, false);
        this.inflater = LayoutInflater.from(activity);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Don't use this to check for the presence of actual data items; use
     * {@link #hasItems()} instead.
     * </p>
     */
    public int getCount() {
        int size = 0;
        if (data != null) {
            size += data.size();
        }
        if (isLoadingData) {
            size += 1;
        }
        return size;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Don't use this to check for the presence of actual data items; use
     * {@link #hasItems()} instead.
     * </p>
     */
    @Override
    public boolean isEmpty() {
        return getCount() == 0 && !isLoadingData;
    }

    /**
     * @return the actual number of data items in this adapter, ignoring the
     *         progress item.
     */
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    /**
     * @return true if there are actual data items, ignoring the progress item.
     */
    public boolean hasItems() {
        return data != null && !data.isEmpty();
    }

    public Object getItem(int position) {
        if (data == null) {
            return null;
        }
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean isEnabled(int position) {
        if (isPositionOfProgressElement(position)) {
            return false;
        }
        return true;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    public void setIsLoadingData(boolean isLoadingData) {
        setIsLoadingData(isLoadingData, true);
    }

    public void setIsLoadingData(boolean isLoadingData, boolean redrawList) {
        this.isLoadingData = isLoadingData;
        if (redrawList) {
            notifyDataSetChanged();
        }
    }

    public boolean isLoadingData() {
        return isLoadingData;
    }

    public final View getView(int position, View convertView, ViewGroup parent) {
        if (isPositionOfProgressElement(position)) {
            return progressView;
        }

        if (convertView == progressView) {
            // make sure the progress view is never used as a convert view
            convertView = null;
        }

        return doGetView(position, convertView, parent);
    }

    protected abstract View doGetView(int position, View convertView, ViewGroup parent);

    private boolean isPositionOfProgressElement(int position) {
        return isLoadingData && position == data.size();
    }

    public List<T> getData() {
        return data;
    }

    public void addAll(List<T> items) {
        data.addAll(items);
        notifyDataSetChanged();
    }

    public void addAll(List<T> items, boolean redrawList) {
        data.addAll(items);
        if (redrawList) {
            notifyDataSetChanged();
        }
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }
}
