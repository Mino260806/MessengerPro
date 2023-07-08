package tn.amin.mpro2.ui.touch;

import android.os.CountDownTimer;
import android.view.MotionEvent;

import androidx.annotation.Nullable;

import tn.amin.mpro2.util.Range;

public class LongPressDetector {
    private static final int DELAY = 1000;

    private CountDownTimer mCountDown;
    private final Range mXRange;
    private final Range mYRange;

    private LongPressListener mListener = null;

    public LongPressDetector(@Nullable Range xRange, @Nullable Range yRange) {
        mXRange = xRange;
        mYRange = yRange;

        mCountDown = new CountDownTimer(DELAY, DELAY) {
            @Override
            public void onTick(long ignored) {}

            @Override
            public void onFinish() {
                if (mListener != null)
                    mListener.onLongPress();
            }
        };
    }

    public void setLongPressListener(LongPressListener mListener) {
        this.mListener = mListener;
    }


    public void handleTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if ((mXRange == null || mXRange.contains(event.getX())) &&
                    (mYRange == null || mYRange.contains(event.getY()))) {
                    mCountDown.start();
                }
                break;
            case MotionEvent.ACTION_UP:
                mCountDown.cancel();
                break;
        }
    }

    public interface LongPressListener {
        void onLongPress();
    }
}
