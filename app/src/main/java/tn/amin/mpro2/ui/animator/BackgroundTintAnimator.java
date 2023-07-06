package tn.amin.mpro2.ui.animator;

import android.animation.Animator;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.res.ColorStateList;
import android.view.View;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

public class BackgroundTintAnimator extends ValueAnimator implements Animator.AnimatorListener {
    private final View mView;
    private final @ColorInt int mColorStart;
    private final @ColorInt int mColorEnd;

    public BackgroundTintAnimator(View view, @ColorInt int colorStart, @ColorInt int colorEnd) {
        mView = view;
        mColorStart = colorStart;
        mColorEnd = colorStart;

        setObjectValues(colorStart, colorEnd);
        setEvaluator(new ArgbEvaluator());

        addListener(this);

        addUpdateListener(valueAnimator -> {
            @ColorInt int color = (int) valueAnimator.getAnimatedValue();
            mView.setBackgroundTintList(ColorStateList.valueOf(color));
        });
    }

    @Override
    public void onAnimationStart(@NonNull Animator animator) {
    }

    @Override
    public void onAnimationEnd(@NonNull Animator animator) {
        @ColorInt int color = (int) getAnimatedValue();
        mView.setBackgroundTintList(ColorStateList.valueOf(color));
    }

    @Override
    public void onAnimationCancel(@NonNull Animator animator) {

    }

    @Override
    public void onAnimationRepeat(@NonNull Animator animator) {

    }
}
