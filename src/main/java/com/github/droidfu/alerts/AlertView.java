package com.github.droidfu.alerts;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.Toast;

import com.github.droidfu.DroidFu;

public class AlertView extends Button implements AnimationListener {

    public static final int STICK_LEFT = 1;
    public static final int STICK_TOP = 2;
    public static final int STICK_RIGHT = 4;
    public static final int STICK_BOTTOM = 8;

    private static HashMap<String, Integer> stickModeMap;

    static {
        stickModeMap = new HashMap<String, Integer>(4);
        stickModeMap.put("left", STICK_LEFT);
        stickModeMap.put("top", STICK_TOP);
        stickModeMap.put("right", STICK_RIGHT);
        stickModeMap.put("bottom", STICK_BOTTOM);
    }

    private View targetView;

    private AnimationSet inAnimation, outAnimation;

    private float offsetLeft, offsetTop;

    private int stickMode = 0;

    private boolean firstLayout = true;

    public AlertView(Context context) {
        super(context);
    }

    public AlertView(Context context, AttributeSet attribs) {
        super(context, attribs);

        // setFocusable(true);
        // setFocusableInTouchMode(true);
        // setClickable(true);

        setVisibility(View.GONE);

        int resId = attribs.getAttributeResourceValue(DroidFu.XMLNS, "targetView", -1);
        targetView = ((Activity) context).findViewById(resId);
        if (targetView == null) {
            throw new IllegalStateException("No target view supplied");
        }

        String stickAttr = attribs.getAttributeValue(DroidFu.XMLNS, "stick");
        if (stickAttr == null) {
            stickMode = STICK_TOP;
        } else {
            String[] values = stickAttr.split("\\|");
            for (String val : values) {
                stickMode |= stickModeMap.get(val);
            }
            System.out.println("stick mode: " + stickMode);
        }
    }

    // @Override
    // protected void onLayout(boolean changed, int left, int top, int right,
    // int bottom) {
    // super.onLayout(changed, left, top, right, bottom);
    //
    // int[] targetViewXY = new int[2];
    // targetView.getLocationInWindow(targetViewXY);
    //
    // // perform Y offset calculations
    // if (stickMode == (stickMode | STICK_TOP)) {
    // System.out.println("TOP");
    // offsetTop = targetViewXY[1] - getMeasuredHeight() -
    // targetView.getMeasuredHeight();
    // } else if (stickMode == (stickMode | STICK_BOTTOM)) {
    // System.out.println("BOTTOM");
    // offsetTop = targetViewXY[1];
    // }
    //
    // if (stickMode == (stickMode | STICK_LEFT)) {
    // System.out.println("LEFT");
    // offsetLeft = targetViewXY[0] - getMeasuredWidth();
    // } else if (stickMode == (stickMode | STICK_RIGHT)) {
    // System.out.println("RIGHT");
    // offsetLeft = targetViewXY[0] + targetView.getMeasuredWidth();
    // } else {
    // System.out.println("CENTER");
    // // center above/below target view
    // float targetCenter = targetView.getMeasuredWidth() / 2.0f;
    // float myCenter = getMeasuredWidth() / 2.0f;
    // offsetLeft = targetViewXY[0] + (targetCenter - myCenter);
    // }
    //
    // System.out.println("offset left: " + offsetLeft);
    // System.out.println("offset top: " + offsetTop);
    //
    // if (firstLayout) {
    // firstLayout = false;
    // loadInAnimation();
    // startAnimation(inAnimation);
    // }
    // }

    // @Override
    // protected void onDraw(Canvas canvas) {
    // super.onDraw(canvas);
    // bringToFront();
    // requestFocus();
    // }

    public AnimationSet loadInAnimation() {
        inAnimation = new AnimationSet(true);
        inAnimation.setFillAfter(true);

        // always load a default translate animation which moves the view
        // to the sticky position
        Animation translate = new TranslateAnimation(0, offsetLeft, 0, offsetTop);
        translate.setDuration(0);
        inAnimation.addAnimation(translate);

        Animation alpha = new AlphaAnimation(0, 1.0f);
        alpha.setDuration(500);
        inAnimation.addAnimation(alpha);

        inAnimation.setAnimationListener(this);

        return inAnimation;
    }

    public void onAnimationEnd(Animation animation) {
        bringToFront();

        setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                System.out.println("CLICK");

                // view.setVisibility(View.GONE);
                // int[] location = new int[2];
                // view.getLocationInWindow(location);
                // Rect rect = new Rect();
                // view.getLocalVisibleRect(rect);
                // view.getParent().invalidateChildInParent(location, rect);
                Toast.makeText(getContext(), "click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub

    }

    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub

    }
}
