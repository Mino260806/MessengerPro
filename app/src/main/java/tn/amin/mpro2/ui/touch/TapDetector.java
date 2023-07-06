package tn.amin.mpro2.ui.touch;

import android.view.MotionEvent;

public class TapDetector {
    OnTapListener mListener = null;
    private boolean mDetectedDown = false;

    public void setOnTapListener(OnTapListener listener) {
        mListener = listener;
    }

    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDetectedDown = true;
                if (mListener != null)
                    mListener.onTapDown();
                break;
            case MotionEvent.ACTION_UP:
                if (mDetectedDown && mListener != null) {
                    mListener.onTapUp();
                    mDetectedDown = false;
                }
                break;
        }
        return false;
    }

    public interface OnTapListener {
        void onTapDown();
        void onTapUp();
    }
}
