package com.github.droidfu.widgets;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import android.widget.ImageView.ScaleType;

import com.github.droidfu.DroidFu;
import com.github.droidfu.imageloader.ImageLoader;
import com.github.droidfu.imageloader.ImageLoaderHandler;

public class GalleryItem extends ViewSwitcher {

    private String imageUrl;

    private boolean isLoaded;

    private ProgressBar loadingSpinner;

    private ImageView imageView;

    private Drawable progressDrawable;

    public GalleryItem(Context context, String imageUrl,
            Drawable progressDrawable) {
        super(context);
        initialize(context, imageUrl, progressDrawable, false);
    }

    public GalleryItem(Context context, AttributeSet attributes) {
        super(context, attributes);
        //TypedArray styles = context.obtainStyledAttributes(attributes,
        //        R.styleable.GalleryItem);
        int progressDrawableId = attributes.getAttributeResourceValue(
                DroidFu.XMLNS, "progressDrawable",
                android.R.drawable.ic_popup_sync);
        initialize(context, attributes.getAttributeValue(DroidFu.XMLNS,
                "imageUrl"), context.getResources().getDrawable(
                progressDrawableId), attributes.getAttributeBooleanValue(
                DroidFu.XMLNS, "autoLoad", true));
        //styles.recycle();
    }

    private void initialize(Context context, String imageUrl,
            Drawable progressDrawable, boolean autoLoad) {
        this.imageUrl = imageUrl;
        this.progressDrawable = progressDrawable;

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(500L);
        setInAnimation(anim);

        addLoadingSpinnerView(context);
        addImageView(context);

        if (autoLoad && imageUrl != null) {
            loadImage();
        }
    }

    private void addLoadingSpinnerView(Context context) {
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);
        loadingSpinner.setIndeterminateDrawable(progressDrawable);
        if (progressDrawable instanceof AnimationDrawable) {
            final AnimationDrawable d = (AnimationDrawable) progressDrawable;
            d.start();
        }
        LayoutParams lp = new LayoutParams(
                progressDrawable.getIntrinsicWidth(),
                progressDrawable.getIntrinsicHeight());
        lp.gravity = Gravity.CENTER;
        addView(loadingSpinner, 0, lp);
    }

    private void addImageView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(ScaleType.CENTER_CROP);
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(imageView, 1, lp);
    }

    public void loadImage() {
        ImageLoader.start(imageUrl, new GalleryImageLoaderHandler());
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        this.progressDrawable = progressDrawable;
    }

    private class GalleryImageLoaderHandler extends ImageLoaderHandler {

        public GalleryImageLoaderHandler() {
            super(imageView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isLoaded = true;

            showNext();
        }
    }
}
