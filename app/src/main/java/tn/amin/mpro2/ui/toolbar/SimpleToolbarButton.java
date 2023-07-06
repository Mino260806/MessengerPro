package tn.amin.mpro2.ui.toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.VectorDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.DrawableCompat;

import tn.amin.mpro2.R;
import tn.amin.mpro2.ui.ModuleResources;
import tn.amin.mpro2.util.BitmapUtil;

@SuppressLint("AppCompatCustomView")
public class SimpleToolbarButton extends View implements View.OnTouchListener {
    public SimpleToolbarButton(@NonNull Context context) {
        super(context);
        init();
    }

    public SimpleToolbarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SimpleToolbarButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    public void setIcon(@DrawableRes int iconId, int size, ModuleResources resources) {
        // Load icon
        Drawable icon = (VectorDrawable) resources.getDrawable(iconId);
        // Set icon size
        icon.setBounds(0, 0, size, size);
        // Set icon fillColor
        ColorFilter colorFilter = new PorterDuffColorFilter(resources.getColor(R.attr.colorOnSurfaceWeak), PorterDuff.Mode.SRC_IN);
        icon.setColorFilter(colorFilter);

        // Load background
        LayerDrawable layerDrawable = (LayerDrawable) resources.getDrawable(R.drawable.toolbar_button);
        setBackground(layerDrawable);

        int iconIndex = layerDrawable.findIndexByLayerId(R.id.toolbar_button_icon);
        // Put icon in background
        layerDrawable.setDrawable(iconIndex, icon);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(200)
                    .start();
                break;

            case MotionEvent.ACTION_UP:
                animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(200)
                        .start();
                break;
        }
        return false;
    }

    public void reloadState() {
    }
}
