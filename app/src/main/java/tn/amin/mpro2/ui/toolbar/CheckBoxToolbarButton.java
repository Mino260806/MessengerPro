package tn.amin.mpro2.ui.toolbar;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.function.Supplier;

import tn.amin.mpro2.R;
import tn.amin.mpro2.ui.ModuleResources;
import tn.amin.mpro2.ui.animator.BackgroundTintAnimator;

public class CheckBoxToolbarButton extends SimpleToolbarButton {
    private Supplier<Boolean> mStateProvider = null;
    public boolean isActivated = false;
    private ActivationListener mListener;
    private int mDeactivatedOverlayColor;

    private ValueAnimator mBackgroundTintAnimator;

    public CheckBoxToolbarButton(@NonNull Context context) {
        super(context);
        init();
    }

    public CheckBoxToolbarButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CheckBoxToolbarButton(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBackgroundTintAnimator = new BackgroundTintAnimator(this, 0, mDeactivatedOverlayColor);
        setBackgroundTintMode(PorterDuff.Mode.SRC_OVER);

        setOnClickListener(view -> {
            isActivated = !isActivated;

            refreshAppearance();
            if (mListener != null)
                mListener.onActivation(isActivated);
        });
    }

    private void refreshAppearance() {
        if (isActivated) {
            mBackgroundTintAnimator.reverse();
        } else {
            mBackgroundTintAnimator.start();
        }
    }

    public void setActivated(boolean activated) {
        isActivated = activated;

        refreshAppearance();
    }

    public void setActivationListener(ActivationListener listener) {
        mListener = listener;
    }

    public interface ActivationListener {
        void onActivation(boolean activated);
    }

    @Override
    public void setIcon(int iconId, int size, ModuleResources resources) {
        mDeactivatedOverlayColor = resources.getColor(R.attr.colorDarkOverlay);
        mBackgroundTintAnimator = new BackgroundTintAnimator(this, 0, mDeactivatedOverlayColor);
        mBackgroundTintAnimator.setDuration(200);

        super.setIcon(iconId, size, resources);
    }

    public void setStateProvider(Supplier<Boolean> stateProvider) {
        mStateProvider = stateProvider;
    }

    @Override
    public void reloadState() {
        if (mStateProvider != null) {
            setActivated(mStateProvider.get());
        }
    }
}
