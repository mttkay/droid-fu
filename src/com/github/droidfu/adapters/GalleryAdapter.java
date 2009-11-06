package com.github.droidfu.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;

import com.github.droidfu.widgets.GalleryItem;

public class GalleryAdapter extends BaseAdapter {

    private List<String> imageUrls;

    private int preferredItemHeight;

    private Context context;

    private Drawable progressDrawable;

    public GalleryAdapter(Context context, List<String> imageUrls,
            int preferredItemHeight, int progressDrawableResId) {
        this.preferredItemHeight = preferredItemHeight;
        this.imageUrls = imageUrls;
        this.context = context;
        this.progressDrawable = context.getResources().getDrawable(
                progressDrawableResId);
    }

    public int getCount() {
        return imageUrls.size();
    }

    public Object getItem(int position) {
        return imageUrls.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        String imageUrl = (String) getItem(position);

        GalleryItem item = new GalleryItem(context, imageUrl, progressDrawable);
        item.setLayoutParams(new Gallery.LayoutParams(
                LayoutParams.WRAP_CONTENT, preferredItemHeight));
        item.loadImage();

        return item;
    }

}
