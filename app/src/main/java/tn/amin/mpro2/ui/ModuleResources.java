package tn.amin.mpro2.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;

import androidx.annotation.AttrRes;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import tn.amin.mpro2.R;
import tn.amin.mpro2.util.DimUtil;

public class ModuleResources {
    private final Resources mResources;
    private final Resources.Theme mTheme;

    public ModuleResources(Context context, Resources resources) {
        mResources = resources;
        mTheme = mResources.newTheme();

        refreshTheme(context);
    }

    public void refreshTheme(Context context) {
        // TODO BUG: this detects system theme, not messenger theme

        int nightModeFlags = context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        boolean isDarkModeEnabled = nightModeFlags == Configuration.UI_MODE_NIGHT_YES;

        int themeResId;
        if (isDarkModeEnabled) {
            themeResId = R.style.AppTheme_Night;
        } else {
            themeResId = R.style.AppTheme;
        }
        mTheme.applyStyle(themeResId, true);
    }

    public Drawable getDrawable(@DrawableRes int id) {
        return ResourcesCompat.getDrawable(mResources, id, mTheme);
    }

    public String getString(@StringRes int id) {
        return mResources.getString(id);
    }

    public String getString(@StringRes int id, Object... formatArgs) {
        return mResources.getString(id, formatArgs);
    }

    public CharSequence getText(@StringRes int id) {
        return mResources.getText(id);
    }

    public int getColor(@AttrRes int id) {
        TypedValue typedValue = new TypedValue();
        mTheme.resolveAttribute(id, typedValue, true);
        @ColorInt int color = typedValue.data;

        return color;
    }

    public int getPixels(int dip) {
        return DimUtil.dipToPixels(mResources, dip);
    }

    public ColorStateList getColorStateList(@ColorRes int id) {
        return mResources.getColorStateList(id, mTheme);
    }

    public Resources unwrap() {
        return mResources;
    }
}
