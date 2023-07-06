package tn.amin.mpro2.ui.toolbar;

import android.app.Activity;
import android.view.MotionEvent;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.ui.touch.MultiFingerSwipeDetector;
import tn.amin.mpro2.ui.touch.SwipeDirection;
import tn.amin.mpro2.ui.touch.TapDetector;
import tn.amin.mpro2.util.DimUtil;

public class ToolbarGestureDetectorHelper implements MultiFingerSwipeDetector.OnSwipeListener, TapDetector.OnTapListener {
    private final SwipeableToolbar mToolbar;
    private final MultiFingerSwipeDetector mSwipeDetector;
    private final TapDetector mTapDetector;

    private final SwipeDirection mSwipeDirection;

    private boolean mWaitingTap = false;

    public ToolbarGestureDetectorHelper(SwipeableToolbar toolbar, int fingersCount, SwipeDirection swipeDirection, boolean fromEdge,
                                        int screenWidth, int screenHeight) {
        mToolbar = toolbar;
        mSwipeDirection = swipeDirection;

        mSwipeDetector = new MultiFingerSwipeDetector(fingersCount, swipeDirection, fromEdge, screenWidth, screenHeight);
        mSwipeDetector.setOnSwipeListener(this);
        mTapDetector = new TapDetector();
        mTapDetector.setOnTapListener(this);
    }

    public boolean onTouchEvent(MotionEvent e) {
        if (mWaitingTap) return mTapDetector.onTouchEvent(e);
        else return mSwipeDetector.onTouchEvent(e);
    }

    @Override
    public void onSwipe() {
        Logger.verbose("Showing toolbar");
        if (mSwipeDirection == SwipeDirection.RIGHT) mToolbar.showFromLeft();
        else mToolbar.showFromRight();
        mWaitingTap = true;
    }

    @Override
    public void onTapDown() {
        Logger.verbose("Hiding toolbar");
        mToolbar.hide();
    }

    @Override
    public void onTapUp() {
        mWaitingTap = false;
        mSwipeDetector.reset();
    }
}
