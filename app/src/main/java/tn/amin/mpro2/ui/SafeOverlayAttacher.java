package tn.amin.mpro2.ui;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import tn.amin.mpro2.debug.Logger;

public class SafeOverlayAttacher implements Runnable {
    private final View mView;
    private final WindowManager.LayoutParams mLayoutParams;
    private final int mDelay;
    private final WindowManager mWindowManager;
    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private OnOverlayAttachedListener mListener = null;
    private boolean shouldAttach = true;


    public SafeOverlayAttacher(View view, WindowManager.LayoutParams layoutParams) {
        this(view, layoutParams, 500);
    }

    public SafeOverlayAttacher(View view, WindowManager.LayoutParams layoutParams, int delay) {
        mView = view;
        mLayoutParams = layoutParams;
        mDelay = delay;

        mWindowManager = (WindowManager) view.getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    public void setOnOverlayAttachedListener(OnOverlayAttachedListener listener) {
        mListener = listener;
    }

    public void attach() {
        mHandler.post(this);
    }

    public void detach() {
        mHandler.removeCallbacks(this);

        shouldAttach = false;
        mHandler.post(this);
    }

    @Override
    public void run() {
        try {
            if (shouldAttach) {
                mWindowManager.addView(mView, mLayoutParams);
                if (mListener != null)
                    mListener.onOverlayAttached();
            } else {
                if (mView.getWindowToken() != null) {
                    mWindowManager.removeView(mView);
                }
            }
        } catch (WindowManager.BadTokenException e) {
            Logger.info("Overlay adding/removing failed, trying again in " + mDelay + "ms...");
            Logger.verbose(Log.getStackTraceString(e));
            mHandler.postDelayed(this, mDelay);
        }
    }

    public interface OnOverlayAttachedListener {
        void onOverlayAttached();
    }
}
