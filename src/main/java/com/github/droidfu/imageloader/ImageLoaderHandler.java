package com.github.droidfu.imageloader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageLoaderHandler extends Handler {

    private ImageView imageView;

    public ImageLoaderHandler(ImageView imageView) {
        this.imageView = imageView;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == ImageLoader.HANDLER_MESSAGE_ID) {
            Bundle data = msg.getData();
            Bitmap bitmap = data.getParcelable(ImageLoader.BITMAP_EXTRA);
            imageView.setImageBitmap(bitmap);
        }
    }

    ImageView getImageView() {
        return imageView;
    }

}
