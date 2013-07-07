package com.github.droidfu.listeners;

import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;

import com.github.droidfu.activities.BetterMapActivity;


public class MapGestureListener extends SimpleOnGestureListener {

    private BetterMapActivity mapActivity;

    public MapGestureListener(BetterMapActivity mapActivity) {
        this.mapActivity = mapActivity;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        mapActivity.getMapView().getController().zoomInFixing((int) event.getX(), (int) event.getY());
        return true;
    }

    public BetterMapActivity getMapActivity() {
        return mapActivity;
    }

}