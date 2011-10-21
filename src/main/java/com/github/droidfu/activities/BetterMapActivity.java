package com.github.droidfu.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ZoomControls;

import com.github.droidfu.DroidFuApplication;
import com.github.droidfu.dialogs.DialogClickListener;
import com.github.droidfu.listeners.MapGestureListener;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class BetterMapActivity extends MapActivity implements BetterActivity {

    private boolean wasCreated, wasInterrupted;

    private int progressDialogTitleId;

    private int progressDialogMsgId;

    private Intent currentIntent;

    private MyLocationOverlay myLocationOverlay;

    private GestureDetector tapDetector;

    private OnTouchListener tapListener;

    private MapView mapView;

    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        this.wasCreated = true;
        this.currentIntent = getIntent();

        Application application = getApplication();
        if (application instanceof DroidFuApplication) {
            ((DroidFuApplication) application).setActiveContext(getClass().getCanonicalName(), this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wasCreated = wasInterrupted = false;

        if (myLocationOverlay != null) {
            myLocationOverlay.disableMyLocation();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (myLocationOverlay != null) {
            myLocationOverlay.enableMyLocation();
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        wasInterrupted = true;
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        this.currentIntent = intent;
    }

    @Override
    protected boolean isRouteDisplayed() {
        return false;
    }

    public Intent getCurrentIntent() {
        return currentIntent;
    }

    public int getWindowFeatures() {
        return BetterActivityHelper.getWindowFeatures(this);
    }

    public boolean isApplicationBroughtToBackground() {
        return BetterActivityHelper.isApplicationBroughtToBackground(this);
    }

    public boolean isLandscapeMode() {
        return getWindowManager().getDefaultDisplay().getOrientation() == 1;
    }

    public boolean isLaunching() {
        return !wasInterrupted && wasCreated;
    }

    public boolean isPortraitMode() {
        return !isLandscapeMode();
    }

    public boolean isRestoring() {
        return wasInterrupted;
    }

    public boolean isResuming() {
        return !wasCreated;
    }

    public AlertDialog newYesNoDialog(int titleResourceId, int messageResourceId,
            OnClickListener listener) {
        return BetterActivityHelper.newYesNoDialog(this, getString(titleResourceId),
            getString(messageResourceId), android.R.drawable.ic_dialog_info, listener);
    }

    public AlertDialog newInfoDialog(int titleResourceId, int messageResourceId) {
        return BetterActivityHelper.newMessageDialog(this, getString(titleResourceId),
            getString(messageResourceId), android.R.drawable.ic_dialog_info);
    }

    public AlertDialog newAlertDialog(int titleResourceId, int messageResourceId) {
        return BetterActivityHelper.newMessageDialog(this, getString(titleResourceId),
            getString(messageResourceId), android.R.drawable.ic_dialog_alert);
    }

    public AlertDialog newErrorHandlerDialog(int titleResourceId, Exception error) {
        return BetterActivityHelper.newErrorHandlerDialog(this, getString(titleResourceId), error);
    }

    public AlertDialog newErrorHandlerDialog(Exception error) {
        return newErrorHandlerDialog(getResources().getIdentifier(
            BetterActivityHelper.ERROR_DIALOG_TITLE_RESOURCE, "string", getPackageName()), error);
    }

    public <T> Dialog newListDialog(String title, List<T> elements,
            DialogClickListener<T> listener,
            boolean closeOnSelect) {
        return BetterActivityHelper.newListDialog(this, title, elements, listener, closeOnSelect);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return BetterActivityHelper.createProgressDialog(this, progressDialogTitleId,
            progressDialogMsgId);
    }

    public void setProgressDialogTitleId(int progressDialogTitleId) {
        this.progressDialogTitleId = progressDialogTitleId;
    }

    public void setProgressDialogMsgId(int progressDialogMsgId) {
        this.progressDialogMsgId = progressDialogMsgId;
    }

    public void setMapView(int mapLayoutId) {
        this.mapView = (MapView) findViewById(mapLayoutId);
    }

    public void setMapViewWithZoom(int mapLayoutId, int zoomControlsLayoutId) {
        this.mapView = (MapView) findViewById(mapLayoutId);

        ZoomControls zoomControls = (ZoomControls) findViewById(zoomControlsLayoutId);
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    getMapView().getController().zoomInFixing(getMapView().getWidth() / 2,
                            getMapView().getHeight() / 2);
                }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getMapView().getController().zoomOut();
            }
        });
    }

    public MapView getMapView() {
        return mapView;
    }

    public void setMyLocationOverlay(MyLocationOverlay myLocationOverlay) {
        // Create the overlay and add it to the map.
        this.myLocationOverlay = myLocationOverlay;
        mapView.getOverlays().add(this.myLocationOverlay);
    }

    public MyLocationOverlay getMyLocationOverlay() {
        return myLocationOverlay;
    }

    protected void setMapGestureListener(MapGestureListener mapGestureListener) {
        // Map gesture listener. Our default implementation handles a double tap action
        // as a zoom in.
        tapDetector = new GestureDetector(mapGestureListener);
        tapListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (tapDetector.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        };
        mapView.setOnTouchListener(tapListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BetterActivityHelper.handleApplicationClosing(this, keyCode);
        return super.onKeyDown(keyCode, event);
    }
}
