package tn.amin.mpro2.ui.touch;

import android.view.MotionEvent;

import tn.amin.mpro2.debug.Logger;

public class MultiFingerSwipeDetector {
    private static final int SWIPE_THRESHOLD = 100;
    private static final int EDGE_THRESHOLD = 50;
    private final int mFingersCount;
    private final SwipeDirection mSwipeDirection;
    private final boolean mFromEdge;
    private final int mScreenWidth;
    private final int mScreenHeight;

    private final Float[] startX;
    private final Float[] startY;
    private boolean mAllowSwipe = false;

    OnSwipeListener mListener = null;

    public MultiFingerSwipeDetector(int fingersCount, SwipeDirection swipeDirection, boolean fromEdge,
                                    int screenWidth, int screenHeight) {
        startX = new Float[fingersCount];
        startY = new Float[fingersCount];
        mFingersCount = fingersCount;
        mSwipeDirection = swipeDirection;
        mFromEdge = fromEdge;

        mScreenWidth = screenWidth;
        mScreenHeight = screenHeight;
    }

    public void setOnSwipeListener(OnSwipeListener listener) {
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        int pointerCount = event.getPointerCount();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (pointerCount == mFingersCount) {
                    for (int i=0; i<pointerCount; i++) {
                        startX[i] = event.getX(i);
                        startY[i] = event.getY(i);

                        if (!areCoordinatesValid(startX[i], startY[i])) {
                            mAllowSwipe = false;
                            return false;
                        }
                    }
                    Logger.verbose("Allowed swipe");
                    mAllowSwipe = true;

                    // If disabled when swiping up from edge, it will disable message composer edit text
                    if (!(mFingersCount == 1 && mSwipeDirection == SwipeDirection.UP && mFromEdge))
                        return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (mAllowSwipe && pointerCount == mFingersCount) {
                    Logger.verbose("Pointer UP");

                    boolean swipeDetected = true;
                    for (int i=0; i<pointerCount; i++) {
                        float endX = event.getX(i);
                        float endY = event.getY(i);
                        float diffX = endX - startX[i];
                        float diffY = endY - startY[i];
                        if (!isCorrectSwipe(diffX, diffY)) {
                            swipeDetected = false;
                            break;
                        }
                    }

                    if (swipeDetected) {
                        if (mListener != null)
                            mListener.onSwipe();

                        Logger.verbose(mFingersCount + "-finger swipe " + mSwipeDirection + " detected");
                        return true;
                    }
                }
                break;
        }

        return false;
    }
    private boolean areCoordinatesValid(float x, float y) {
        if (mFromEdge) {
            if (mSwipeDirection == SwipeDirection.LEFT && x < mScreenWidth - EDGE_THRESHOLD) return false;
            else if (mSwipeDirection == SwipeDirection.RIGHT && x > EDGE_THRESHOLD) return false;
            else if (mSwipeDirection == SwipeDirection.UP && y < mScreenHeight) return false;
            else if (mSwipeDirection == SwipeDirection.DOWN && y > EDGE_THRESHOLD) return false;
        }

        return true;
    }

    private boolean isCorrectSwipe(float diffX, float diffY) {
        switch (mSwipeDirection) {
            case LEFT:
                return diffX < 0 &&
                        Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > SWIPE_THRESHOLD;
            case RIGHT:
                return diffX > 0 &&
                        Math.abs(diffX) > Math.abs(diffY) &&
                        Math.abs(diffX) > SWIPE_THRESHOLD;
            case UP:
                return diffY < 0 &&
                        Math.abs(diffY) > Math.abs(diffX) &&
                        Math.abs(diffY) > SWIPE_THRESHOLD;
            case DOWN:
                return diffY > 0 &&
                        Math.abs(diffY) > Math.abs(diffX) &&
                        Math.abs(diffY) > SWIPE_THRESHOLD;
        }
        return false;
    }

    public void reset() {
        mAllowSwipe = false;
    }

    public interface OnSwipeListener {
        void onSwipe();
    }
}
