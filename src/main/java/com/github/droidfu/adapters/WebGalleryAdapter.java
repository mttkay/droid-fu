package com.github.droidfu.adapters;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;

import com.github.droidfu.widgets.WebImageView;

/**
 * Can be used as an adapter for an Android {@link Gallery} view. This adapter
 * loads the images to be shown from the web.
 * 
 * @author Matthias Kaeppler
 */
public class WebGalleryAdapter extends BaseAdapter {

    private List<String> imageUrls;

    private Context context;

    private Drawable progressDrawable;

    public WebGalleryAdapter(Context context) {
        initialize(context, null, null);
    }

    /**
     * @param context
     *        the current context
     * @param imageUrls
     *        the set of image URLs which are to be loaded and displayed
     */
    public WebGalleryAdapter(Context context, List<String> imageUrls) {
        initialize(context, imageUrls, null);
    }

    /**
     * @param context
     *        the current context
     * @param imageUrls
     *        the set of image URLs which are to be loaded and displayed
     * @param progressDrawableResId
     *        the resource ID of the drawable that will be used for rendering
     *        progress
     */
    public WebGalleryAdapter(Context context, List<String> imageUrls, int progressDrawableResId) {
        initialize(context, imageUrls, context.getResources().getDrawable(progressDrawableResId));
    }

    private void initialize(Context context, List<String> imageUrls, Drawable progressDrawable) {
        this.imageUrls = imageUrls;
        this.context = context;
        this.progressDrawable = progressDrawable;
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

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        this.progressDrawable = progressDrawable;
    }

    public Drawable getProgressDrawable() {
        return progressDrawable;
    }

    // TODO: leverage convert view for better performance
    public View getView(int position, View convertView, ViewGroup parent) {

        String imageUrl = (String) getItem(position);

        FrameLayout container = new FrameLayout(context);
        container.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT,
                LayoutParams.FILL_PARENT));

        WebImageView item = new WebImageView(context, imageUrl, progressDrawable, false);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        item.setLayoutParams(lp);

        container.addView(item);

        item.loadImage();

        onGetView(position, convertView, parent);

        return container;
    }

    protected void onGetView(int position, View convertView, ViewGroup parent) {
    }
}
