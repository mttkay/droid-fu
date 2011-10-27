package com.github.droidfu.activities;

import java.util.List;

import android.app.AlertDialog;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ZoomControls;

import com.github.droidfu.DroidFuApplication;
import com.github.droidfu.dialogs.DialogClickListener;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

public class BetterMapActivity extends MapActivity implements BetterActivity {
    
    private boolean wasCreated, wasInterrupted;
    
    private int progressDialogTitleId;

    private int progressDialogMsgId;
    
    private Intent currentIntent;
    
    private MyLocationOverlay myLocationOverlay;

    private MapView mapView;
    
    private ZoomControls zoomControls;

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

    @Override
    public Intent getCurrentIntent() {
        return currentIntent;
    }

    @Override
    public int getWindowFeatures() {
        return BetterActivityHelper.getWindowFeatures(this);
    }

    @Override
    public boolean isApplicationBroughtToBackground() {
        return BetterActivityHelper.isApplicationBroughtToBackground(this);
    }

    @Override
    public boolean isLandscapeMode() {
        return getWindowManager().getDefaultDisplay().getOrientation() == 1;
    }

    @Override
    public boolean isLaunching() {
        return !wasInterrupted && wasCreated;
    }

    @Override
    public boolean isPortraitMode() {
        return !isLandscapeMode();
    }

    @Override
    public boolean isRestoring() {
        return wasInterrupted;
    }

    @Override
    public boolean isResuming() {
        return !wasCreated;
    }

    @Override
    public AlertDialog newYesNoDialog(int titleResourceId,
            int messageResourceId, OnClickListener listener) {
        return BetterActivityHelper.newYesNoDialog(this,
                getString(titleResourceId), getString(messageResourceId),
                android.R.drawable.ic_dialog_info, listener);
    }

    @Override
    public AlertDialog newInfoDialog(int titleResourceId, int messageResourceId) {
        return BetterActivityHelper.newMessageDialog(this,
                getString(titleResourceId), getString(messageResourceId),
                android.R.drawable.ic_dialog_info);
    }

    @Override
    public AlertDialog newAlertDialog(int titleResourceId, int messageResourceId) {
        return BetterActivityHelper.newMessageDialog(this,
                getString(titleResourceId), getString(messageResourceId),
                android.R.drawable.ic_dialog_alert);
    }

    @Override
    public AlertDialog newErrorHandlerDialog(int titleResourceId,
            Exception error) {
        return BetterActivityHelper.newErrorHandlerDialog(this,
                getString(titleResourceId), error);
    }

    @Override
    public AlertDialog newErrorHandlerDialog(Exception error) {
        return newErrorHandlerDialog(
                getResources().getIdentifier(
                        BetterActivityHelper.ERROR_DIALOG_TITLE_RESOURCE,
                        "string", getPackageName()), error);
    }

    @Override
    public <T> Dialog newListDialog(String title, List<T> elements,
            DialogClickListener<T> listener, boolean closeOnSelect) {
        return BetterActivityHelper.newListDialog(this, title, elements,
                listener, closeOnSelect);
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        return BetterActivityHelper.createProgressDialog(this,
                progressDialogTitleId, progressDialogMsgId);
    }

    @Override
    public void setProgressDialogTitleId(int progressDialogTitleId) {
        this.progressDialogTitleId = progressDialogTitleId;
    }

    @Override
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
            @Override
            public void onClick(View v) {
                getMapView().getController().zoomInFixing(getMapView().getWidth() / 2,
                        getMapView().getHeight() / 2);
            }
        });
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMapView().getController().zoomOut();
            }
        });
    }
    
    public void setMapViewWithZoom(int mapLayoutId, int zoomControlsLayoutId,
            View.OnClickListener zoomInOnCLickListener,
            View.OnClickListener zoomOutOnCLickListener) {
        setMapView(mapLayoutId);
        setZoomControls(zoomControlsLayoutId, zoomInOnCLickListener,
                zoomOutOnCLickListener);
    }

    public MapView getMapView() {
        return mapView;
    }
    
    /**
     * Sets zoom controls for this map view.
     * 
     * @param zoomControlsLayoutId
     *            the id of the zoom controls layout
     * @param zoomControlsOnCLickListener
     *            the listener that will handle zoom in/out actions. If null
     *            then the standard one will be set
     */
    public void setZoomControls(int zoomControlsLayoutId,
            View.OnClickListener zoomInOnCLickListener,
            View.OnClickListener zoomOutOnCLickListener) {
        zoomControls = (ZoomControls) findViewById(zoomControlsLayoutId);
        zoomControls.setOnZoomInClickListener(zoomInOnCLickListener);
        zoomControls.setOnZoomOutClickListener(zoomOutOnCLickListener);
    }

    public ZoomControls getZoomControls() {
        return zoomControls;
    }

    public void setZoomControlsListeners(View.OnClickListener onZoomListener) {
    }

    public void setMyLocationOverlay(MyLocationOverlay myLocationOverlay) {
        // Create the overlay and add it to the map.
        this.myLocationOverlay = myLocationOverlay;
        mapView.getOverlays().add(this.myLocationOverlay);
    }
    
    public MyLocationOverlay getMyLocationOverlay() {
        return myLocationOverlay;
    }

    protected void setMapGestureListener(OnGestureListener mapOnGestureListener) {
        // Map gesture listener. Our default implementation handles a double tap
        // action as a zoom in.
        final GestureDetector gestureDetector = new GestureDetector(this, mapOnGestureListener);
        OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        mapView.setOnTouchListener(onTouchListener);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        BetterActivityHelper.handleApplicationClosing(this, keyCode);
        return super.onKeyDown(keyCode, event);
    }
}
