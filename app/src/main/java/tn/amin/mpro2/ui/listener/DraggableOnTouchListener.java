package tn.amin.mpro2.ui.listener;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import tn.amin.mpro2.util.Range;

public class DraggableOnTouchListener implements View.OnTouchListener {
    private final OnPositionChangeListener mListener;
    private final View mView;

    float prevTouchX = 0;
    float prevTouchY = 0;

    float prevMarginX = 0;
    float prevMarginY = 0;

    private boolean mLockX = false;
    private boolean mLockY = false;
    private Range mXRange = null;
    private Range mYRange = null;

    public DraggableOnTouchListener(View view, OnPositionChangeListener listener) {
        mListener = listener;
        mView = view;
    }

    public void lock(boolean lockX, boolean lockY) {
        mLockX = lockX;
        mLockY = lockY;
    }

    public void setXRange(Range xRange) {
        mXRange = xRange;
    }

    public void setYRange(Range yRange) {
        mYRange = yRange;
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        WindowManager windowManager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();

        float touchX = event.getRawX();
        float touchY = event.getRawY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                prevTouchX = touchX;
                prevTouchY = touchY;

                prevMarginX = layoutParams.x;
                prevMarginY = layoutParams.y;
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_MOVE:
                if (!mLockX) {
                    float diffX = touchX - prevTouchX;
                    int newX = (int) (prevMarginX + diffX);
                    if (mXRange != null)
                        newX = mXRange.clamp(newX).intValue();
                    layoutParams.x = newX;
                }

                if (!mLockY) {
                    float diffY = touchY - prevTouchY;
                    int newY = (int) (prevMarginY + diffY);
                    if (mYRange != null)
                        newY = mYRange.clamp(newY).intValue();
                    layoutParams.y = newY;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {
                    mListener.onPositionChange(layoutParams.x, layoutParams.y);
                }

                windowManager.updateViewLayout(view, layoutParams);
                break;
        }

        return false;
    }

    public interface OnPositionChangeListener {
        void onPositionChange(int x, int y);
    }
}
