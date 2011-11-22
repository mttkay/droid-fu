package com.github.droidfu.listeners;

import com.github.droidfu.activities.BetterMapActivity;

import android.view.MotionEvent;
import android.view.GestureDetector.SimpleOnGestureListener;


public class MapGestureListener extends SimpleOnGestureListener {

    protected BetterMapActivity mapActivity;

    public MapGestureListener(BetterMapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        mapActivity.getMapView().getController().zoomInFixing((int) event.getX(), (int) event.getY());
        return true;
    }

}