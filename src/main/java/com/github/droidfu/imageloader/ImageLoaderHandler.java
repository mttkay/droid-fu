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

package com.github.droidfu.imageloader;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

public class ImageLoaderHandler extends Handler {

    private ImageView imageView;
    private Integer position;

    public ImageLoaderHandler(ImageView imageView) {
        this.imageView = imageView;
    }
    
    public ImageLoaderHandler(ImageView imageView, int position) {
        this.imageView = imageView;
        this.position = position;
    }

    @Override
    public void handleMessage(Message msg) {
        if (msg.what == ImageLoader.HANDLER_MESSAGE_ID) {
            handleImageLoadedMessage(msg);
        }
    }

    protected void handleImageLoadedMessage(Message msg) {
        // If this handler is used for loading images in a ListAdapter,
        // the thread will set the image only if it's the right position,
        // otherwise it won't do anything.
        if (position != null) {
            int forPosition = (Integer) imageView.getTag();
            if (forPosition != this.position) {
                return;
            }
        }
        Bundle data = msg.getData();
        Bitmap bitmap = data.getParcelable(ImageLoader.BITMAP_EXTRA);
        imageView.setImageBitmap(bitmap);
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }
}
