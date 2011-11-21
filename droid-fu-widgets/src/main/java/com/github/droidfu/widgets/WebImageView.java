/* Copyright (c) 2009 Matthias Kaeppler
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

package com.github.droidfu.widgets;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ViewSwitcher;
import android.widget.ImageView.ScaleType;

import com.github.droidfu.DroidFu;
import com.github.droidfu.imageloader.ImageLoader;
import com.github.droidfu.imageloader.ImageLoaderHandler;

/**
 * An image view that fetches its image off the web using the supplied URL. While the image is being
 * downloaded, a progress indicator will be shown.
 * 
 * @author Matthias Kaeppler
 */
public class WebImageView extends ViewSwitcher {

    private String imageUrl;

    private boolean isLoaded;

    private ProgressBar loadingSpinner;

    private ImageView imageView;

    private ScaleType scaleType = ScaleType.CENTER_CROP;

    private Drawable progressDrawable, errorDrawable;

    /**
     * @param context
     *            the view's current context
     * @param imageUrl
     *            the URL of the image to download and show
     * @param autoLoad
     *            Whether the download should start immediately after creating the view. If set to
     *            false, use {@link #loadImage()} to manually trigger the image download.
     */
    public WebImageView(Context context, String imageUrl, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, null, null, autoLoad);
    }

    /**
     * @param context
     *            the view's current context
     * @param imageUrl
     *            the URL of the image to download and show
     * @param progressDrawable
     *            the drawable to be used for the {@link ProgressBar} which is displayed while the
     *            image is loading
     * @param autoLoad
     *            Whether the download should start immediately after creating the view. If set to
     *            false, use {@link #loadImage()} to manually trigger the image download.
     */
    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
            boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, null, autoLoad);
    }

    /**
     * @param context
     *            the view's current context
     * @param imageUrl
     *            the URL of the image to download and show
     * @param progressDrawable
     *            the drawable to be used for the {@link ProgressBar} which is displayed while the
     *            image is loading
     * @param errorDrawable
     *            the drawable to be used if a download error occurs
     * @param autoLoad
     *            Whether the download should start immediately after creating the view. If set to
     *            false, use {@link #loadImage()} to manually trigger the image download.
     */
    public WebImageView(Context context, String imageUrl, Drawable progressDrawable,
            Drawable errorDrawable, boolean autoLoad) {
        super(context);
        initialize(context, imageUrl, progressDrawable, errorDrawable, autoLoad);
    }

    public WebImageView(Context context, AttributeSet attributes) {
        super(context, attributes);
        // TypedArray styles = context.obtainStyledAttributes(attributes,
        // R.styleable.GalleryItem);
        int progressDrawableId = attributes.getAttributeResourceValue(DroidFu.XMLNS,
                "progressDrawable", 0);
        int errorDrawableId = attributes.getAttributeResourceValue(DroidFu.XMLNS, "errorDrawable",
                0);
        Drawable progressDrawable = null;
        if (progressDrawableId > 0) {
            progressDrawable = context.getResources().getDrawable(progressDrawableId);
        }
        Drawable errorDrawable = null;
        if (errorDrawableId > 0) {
            errorDrawable = context.getResources().getDrawable(errorDrawableId);
        }
        initialize(context, attributes.getAttributeValue(DroidFu.XMLNS, "imageUrl"),
                progressDrawable, errorDrawable, attributes.getAttributeBooleanValue(
                        DroidFu.XMLNS, "autoLoad",
                        true));
        // styles.recycle();
    }

    private void initialize(Context context, String imageUrl, Drawable progressDrawable,
            Drawable errorDrawable,
            boolean autoLoad) {
        this.imageUrl = imageUrl;
        this.progressDrawable = progressDrawable;
        this.errorDrawable = errorDrawable;

        ImageLoader.initialize(context);

        // ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
        // 125.0f, preferredItemHeight / 2.0f);
        // anim.setDuration(500L);

        // AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        // anim.setDuration(500L);
        // setInAnimation(anim);

        addLoadingSpinnerView(context);
        addImageView(context);

        if (autoLoad && imageUrl != null) {
            loadImage();
        }
    }

    private void addLoadingSpinnerView(Context context) {
        loadingSpinner = new ProgressBar(context);
        loadingSpinner.setIndeterminate(true);
        if (this.progressDrawable == null) {
            this.progressDrawable = loadingSpinner.getIndeterminateDrawable();
        } else {
            loadingSpinner.setIndeterminateDrawable(progressDrawable);
            if (progressDrawable instanceof AnimationDrawable) {
                ((AnimationDrawable) progressDrawable).start();
            }
        }

        LayoutParams lp = new LayoutParams(progressDrawable.getIntrinsicWidth(), progressDrawable
                .getIntrinsicHeight());
        lp.gravity = Gravity.CENTER;

        addView(loadingSpinner, 0, lp);
    }

    private void addImageView(Context context) {
        imageView = new ImageView(context);
        imageView.setScaleType(scaleType);
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(imageView, 1, lp);
    }

    /**
     * Use this method to trigger the image download if you had previously set autoLoad to false.
     */
    public void loadImage() {
        if (imageUrl == null) {
            throw new IllegalStateException(
                    "image URL is null; did you forget to set it for this view?");
        }
        ImageLoader.start(imageUrl, new DefaultImageLoaderHandler());
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    /**
     * Often you have resources which usually have an image, but some don't. For these cases, use
     * this method to supply a placeholder drawable which will be loaded instead of a web image.
     * 
     * @param imageResourceId
     *            the resource of the placeholder image drawable
     */
    public void setNoImageDrawable(int imageResourceId) {
        imageView.setImageDrawable(getContext().getResources().getDrawable(imageResourceId));
        setDisplayedChild(1);
    }

    @Override
    public void reset() {
        super.reset();

        this.setDisplayedChild(0);
    }

    private class DefaultImageLoaderHandler extends ImageLoaderHandler {

        public DefaultImageLoaderHandler() {
            super(imageView, imageUrl, errorDrawable);
        }

        @Override
        protected boolean handleImageLoaded(Bitmap bitmap, Message msg) {
            boolean wasUpdated = super.handleImageLoaded(bitmap, msg);
            if (wasUpdated) {
                isLoaded = true;
                setDisplayedChild(1);
            }
            return wasUpdated;
        }
    }

    /**
     * Returns the URL of the image to show
     * @return
     */
	public String getImageUrl() {
		return imageUrl;
	}
}
