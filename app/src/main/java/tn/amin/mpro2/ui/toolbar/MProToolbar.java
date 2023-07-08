package tn.amin.mpro2.ui.toolbar;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureManager;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.action.SettingsFeature;
import tn.amin.mpro2.preference.ModulePreferences;
import tn.amin.mpro2.ui.listener.DraggableOnTouchListener;
import tn.amin.mpro2.ui.ModuleResources;
import tn.amin.mpro2.ui.touch.SwipeDirection;
import tn.amin.mpro2.util.Range;

public class MProToolbar extends LinearLayout implements
        DraggableOnTouchListener.OnPositionChangeListener,
        SwipeableToolbar {
    private static final int TOOLBAR_WIDTH_DP = 70;
    private static final int TOOLBAR_MARGIN_DP = 25;
    private static final int BUTTON_MARGIN_DP = 12;
    private static final int BUTTON_SIZE_DP = TOOLBAR_WIDTH_DP - BUTTON_MARGIN_DP * 2;

    public int toolbarWidth;
    public int toolbarMargin;
    public int buttonMargin;
    public int buttonSize;

    private boolean mInitialized = false;
    private ArrayList<Runnable> mInitializedCallbacks = new ArrayList<>();

    private DraggableOnTouchListener mOnTouchListener;
    private Listener mListener;

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    private int mScreenWidth;
    private int mScreenHeight;

    private final ArrayList<SimpleToolbarButton> mButtons = new ArrayList<>();
    private final ArrayList<View> mSeparators = new ArrayList<>();
    private final ArrayList<String> mNextSeparatorKeys = new ArrayList<>();
    private boolean mShownFromRight = false;

    private ToolbarGestureDetectorHelper mGestureDetectorHelper = null;
    private SummonPropertiesProvider mSummonPropertiesProvider;
    private VisibilityProvider mVisibilityProvider;

    private ModuleResources resources;


    public MProToolbar(Context context) {
        super(context);
    }

    public MProToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MProToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MProToolbar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void init(ModuleResources resources, FeatureManager featureManager) {
        this.resources = resources;

        mWindowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;

        toolbarWidth = resources.getPixels(TOOLBAR_WIDTH_DP);
        toolbarMargin = resources.getPixels(TOOLBAR_MARGIN_DP);
        buttonMargin = resources.getPixels(BUTTON_MARGIN_DP);
        buttonSize = resources.getPixels(BUTTON_SIZE_DP);

        mOnTouchListener = new DraggableOnTouchListener(this, this);
        mOnTouchListener.lock(true, false);
        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                if (getHeight() > 0) {
                    mOnTouchListener.setYRange(new Range(0, mScreenHeight - getHeight()));
                    getViewTreeObserver().removeOnPreDrawListener(this);
                }
                return true;
            }
        });
        setOnTouchListener(mOnTouchListener);
        setOrientation(LinearLayout.VERTICAL);
        setBackground(resources.getDrawable(R.drawable.toolbar_background));

        for (ToolbarButtonCategory category: ToolbarButtonCategory.values()) {
            for (Feature feature: featureManager.getByCategory(category)) {
                if (feature.getType() == FeatureType.CHECKABLE_STATE) {
                    CheckBoxToolbarButton button = addCheckboxButton(
                            Objects.requireNonNull(feature.getDrawableResource()),
                            feature.getPreferenceKey(),
                            feature.getToolbarDescription());
                    button.setStateProvider(feature::isEnabled);
                    button.setActivationListener(feature::setEnabled);
                }
                else if (feature.getType() == FeatureType.ACTION) {
                    boolean last = false;
                    if (feature instanceof SettingsFeature)
                        last = true;
                    SimpleToolbarButton button = addNormalButton(
                            Objects.requireNonNull(feature.getDrawableResource()),
                            feature.getPreferenceKey(),
                            feature.getToolbarDescription(),
                            last);
                    button.setOnClickListener(view -> {
                        hide();
                        feature.executeAction();
                    });
                } else {
                    Logger.warn("MProToolbar: Ignoring unsupported FeatureType " + feature.getType());
                }
            }

            if (category != ToolbarButtonCategory.SETTINGS) {
                addSeperator();
            }
        }
    }

    public boolean handleTouchEvent(MotionEvent event) {
        return mGestureDetectorHelper.onTouchEvent(event);
    }

    private void confirmInitialized() {
        mInitialized = true;
        for (Runnable callback: mInitializedCallbacks) {
            callback.run();
        }
        mInitializedCallbacks.clear();
    }

    private void doOnInitialized(Runnable callback) {
        if (mInitialized) {
            callback.run();
        } else {
            mInitializedCallbacks.add(callback);
        }
    }

    private void setVisibilityProvider(VisibilityProvider provider) {
        mVisibilityProvider = provider;

        reloadVisibility();
    }

    private void setSummonPropertiesProvider(SummonPropertiesProvider provider) {
        mSummonPropertiesProvider = provider;

        reloadSummonProperties();
    }

    private void addSeperator() {
        LayoutParams sepParams = new LayoutParams(buttonSize, 10);
        sepParams.setMargins(buttonMargin, buttonMargin, buttonMargin,0);

        View sep = new View(getContext());
        sep.setBackgroundColor(resources.getColor(R.attr.colorSurfaceWeak));
        addView(sep, sepParams);

        sep.setTag(mNextSeparatorKeys.clone());
        mNextSeparatorKeys.clear();
        mSeparators.add(sep);
    }

    private SimpleToolbarButton addNormalButton(@DrawableRes int iconId, String key, @Nullable @StringRes Integer description) {
        return addNormalButton(iconId, key, description, false);
    }

    private SimpleToolbarButton addNormalButton(@DrawableRes int iconId, String key, @Nullable @StringRes Integer description, boolean last) {
        SimpleToolbarButton button = new SimpleToolbarButton(getContext());

        button.setIcon(iconId, buttonSize, resources);
        button.setTag(key);
        configAndAddButton(button, description, last);

        mButtons.add(button);
        mNextSeparatorKeys.add(key);

        return button;
    }

    private CheckBoxToolbarButton addCheckboxButton(@DrawableRes int iconId, String key, @Nullable @StringRes Integer description) {
        return addCheckboxButton(iconId, key, description, false);
    }

    private CheckBoxToolbarButton addCheckboxButton(@DrawableRes int iconId, String key, @Nullable @StringRes Integer description, boolean last) {
        CheckBoxToolbarButton button = new CheckBoxToolbarButton(getContext());

        button.setIcon(iconId, buttonSize, resources);
        button.setTag(key);
        configAndAddButton(button, description, last);

        mButtons.add(button);
        mNextSeparatorKeys.add(key);

        return button;
    }

    private void configAndAddButton(View button, @Nullable @StringRes Integer description, boolean last) {
        button.setForegroundGravity(Gravity.CENTER);
        button.setLongClickable(true);
        if (description != null) {
            button.setOnLongClickListener((b) -> {
                new AlertDialog.Builder(getContext())
                        .setTitle(resources.getText(R.string.feature_info))
                        .setMessage(resources.getText(description))
                        .setPositiveButton(resources.getString(android.R.string.ok), (d, i) -> {})
                        .show();
                return true;
            });
        }

        addView(button, getButtonLayoutParams(last));
    }

    private void addMoveView() {
        TextView moveView = new TextView(getContext());
        moveView.setBackgroundResource(android.R.drawable.button_onoff_indicator_off);
        addView(moveView, getButtonLayoutParams(true));
    }

    private LayoutParams getButtonLayoutParams(boolean last) {
        LayoutParams params = new LayoutParams(buttonSize, buttonSize);
        int bottomMargins = last? buttonMargin: 0;
        params.setMargins(buttonMargin, buttonMargin, buttonMargin, bottomMargins);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        return params;
    }

    public void setListener(Listener listener) {
        mListener = listener;
    }

    @Override
    public void onPositionChange(int x, int y) {
        mListener.onToolbarPositionChanged(x, y);
    }

    @Override
    public void showFromRight() {
        animateShowHide(new Range(mScreenWidth + toolbarMargin, mScreenWidth - toolbarMargin - toolbarWidth), true);
        mShownFromRight = true;
    }

    @Override
    public void showFromLeft() {
        animateShowHide(new Range(-toolbarMargin - toolbarWidth, toolbarMargin), true);
    }

    private void hideFromRight() {
        animateShowHide(new Range(mScreenWidth + toolbarMargin, mScreenWidth - toolbarMargin - toolbarWidth), false);
        mShownFromRight = false;
    }

    private void hideFromLeft() {
        animateShowHide(new Range(-toolbarMargin - toolbarWidth, toolbarMargin), false);
    }

    @Override
    public void hide() {
        if (mShownFromRight) hideFromRight();
        else hideFromLeft();
    }

    private void animateShowHide(Range xRange, boolean show) {
        doOnInitialized(() -> {
            ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f);
            animator.addUpdateListener(valueAnimator -> {
                float value = (float) valueAnimator.getAnimatedValue();
                mLayoutParams.x = new Range(0, 1).transform(value, xRange).intValue();
                mWindowManager.updateViewLayout(this, mLayoutParams);

                setAlpha(value);
            });

            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(@NonNull Animator animator) {
                    if (show) setVisibility(VISIBLE);
                }

                @Override
                public void onAnimationEnd(@NonNull Animator animator) {
                    if (!show) setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(@NonNull Animator animator) {
                }

                @Override
                public void onAnimationRepeat(@NonNull Animator animator) {

                }
            });
            animator.setDuration(300);
            animator.setInterpolator(new DecelerateInterpolator());
            if (show)
                animator.start();
            else
                animator.reverse();
        });
    }

    public interface VisibilityProvider {
        boolean isButtonVisible(String key);
    }
    public interface SummonPropertiesProvider {

        int getFingersCount();
        SwipeDirection getSwipeDirection();
        boolean getFromEdge();
    }
    public interface Listener {

        void onToolbarPositionChanged(int x, int y);
    }
    public static MProToolbar summon(Activity activity, ModulePreferences pref, ModuleResources resources,
                                     FeatureManager featureManager, Listener listener, int initialX, int initialY) {
        MProToolbar toolbar = new MProToolbar(activity);
        toolbar.init(resources, featureManager);
        toolbar.setListener(listener);
        toolbar.setVisibilityProvider(new MProToolbarVisibilityProvider(pref));
        toolbar.setSummonPropertiesProvider(new MProToolbarSummonPropertiesProvider(pref));

        toolbar.attachToWindow(activity, initialX, initialY);
        return toolbar;
    }

    public void attachToWindow(Activity activity, int initialX, int initialY) {
        WindowAttacher windowAttacher = new WindowAttacher(activity, initialX, initialY);
        windowAttacher.attach();
    }

    public void reloadAll() {
        reloadState();
        reloadVisibility();
        reloadSummonProperties();
    }

    public void reloadState() {
        for (SimpleToolbarButton button: mButtons) {
            button.reloadState();
        }
    }

    public void reloadVisibility() {
        for (View button: mButtons) {
            String key = (String) button.getTag();
            boolean isVisible = mVisibilityProvider.isButtonVisible(key);

            button.setVisibility(isVisible? VISIBLE: GONE);
        }

        for (View sep: mSeparators) {
            List<String> relatedKeys = (List<String>) sep.getTag();
            boolean isVisible = false;
            for (String relatedKey: relatedKeys) {
                if (mVisibilityProvider.isButtonVisible(relatedKey)) {
                    isVisible = true;
                    break;
                }
            }

            sep.setVisibility(isVisible? VISIBLE: GONE);
        }
    }

    private void reloadSummonProperties() {
        mGestureDetectorHelper = new ToolbarGestureDetectorHelper(this,
                mSummonPropertiesProvider.getFingersCount(),
                mSummonPropertiesProvider.getSwipeDirection(),
                mSummonPropertiesProvider.getFromEdge(),
                mScreenWidth, mScreenHeight);
    }

    private class WindowAttacher implements Runnable {
        private final Activity activity;
        private final int initialY;

        private final int initialX;

        private final Handler mHandler = new Handler(Looper.getMainLooper());

        public WindowAttacher(Activity activity, int initialX, int initialY) {
            this.activity = activity;
            this.initialX = initialX;
            this.initialY = initialY;
        }

        public void attach() {
            mHandler.postDelayed(this, 500);
        }
        @Override
        public void run() {
            mLayoutParams = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);

            mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
            mLayoutParams.x = initialX;
            mLayoutParams.y = initialY;

            try {
                mWindowManager.addView(MProToolbar.this, mLayoutParams);
                confirmInitialized();
            }
            catch (WindowManager.BadTokenException e) {
                Logger.info("Toolbar adding failed, trying again in 1s...");
                mHandler.postDelayed(this, 1000);
            }
        }

    }
}
