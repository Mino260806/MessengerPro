package tn.amin.mpro2.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.WindowManager;

public class BlackOverlay {
    public static View on(Context context) {
        View blackOverlay = new View(context);
        blackOverlay.setBackgroundColor(Color.BLACK);
        blackOverlay.setAlpha(1f);

        SafeOverlayAttacher attacher = new SafeOverlayAttacher(blackOverlay, getLayoutParams());
        blackOverlay.setTag(attacher);
        attacher.attach();

        return blackOverlay;
    }

    public static void remove(View blackOverlay) {
        SafeOverlayAttacher attacher = (SafeOverlayAttacher) blackOverlay.getTag();
        attacher.detach();
    }

    public static WindowManager.LayoutParams getLayoutParams() {
        return new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
        );
    }
}
