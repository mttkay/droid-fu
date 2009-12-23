package com.github.droidfu.support;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;

public class DisplaySupport {

    private static DisplayMetrics displayMetrics;

    public static int dipToPx(Activity context, int dip) {
        if (displayMetrics == null) {
            displayMetrics = new DisplayMetrics();
            context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        }
        return (int) (dip * displayMetrics.density + 0.5f);
    }

    public static Drawable scaleDrawable(Context context, int drawableResourceId, int width,
            int height) {
        Bitmap sourceBitmap = BitmapFactory.decodeResource(context.getResources(),
            drawableResourceId);
        return new BitmapDrawable(Bitmap.createScaledBitmap(sourceBitmap, width, height, true));
    }

}