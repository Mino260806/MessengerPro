package tn.amin.mpro.internal.ui;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import androidx.annotation.DrawableRes;
import androidx.core.content.res.ResourcesCompat;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;

public class TitleBarButtonsExtender {
    ArrayList<TitleBarButtonModel> mButtonModels = new ArrayList<>();

    public void extend(ArrayList inputButtons) throws InstantiationException, IllegalAccessException {
        Bitmap bitmapModel = ((BitmapDrawable) XposedHelpers.getObjectField(XposedHelpers.getObjectField(inputButtons.get(0), "A01"), "A04")).getBitmap();
        int dpi = bitmapModel.getDensity();
        int width = bitmapModel.getWidth();
        int height = bitmapModel.getHeight();

        for (TitleBarButtonModel buttonModel: mButtonModels) {
            BitmapDrawable drawable = buttonModel.getIcon();
            Bitmap b = drawable.getBitmap();
            Bitmap bitmapResized = Bitmap.createScaledBitmap(b, width / 2, height / 2, false);
            bitmapResized.setDensity(dpi);
            Drawable buttonIcon = new BitmapDrawable(MProMain.getMProResources(), bitmapResized);

            // It would have been easier to use XposedHelpers.findClass but using getParameterTypes()
            // will save time when updating obfuscated classes for a newer version
            Object buttonDataModel = inputButtons.get(0);
            Object buttonIconDataModel = XposedHelpers.getObjectField(buttonDataModel, "A01");
            Object buttonIconData = XposedHelpers.newInstance(buttonIconDataModel.getClass(), buttonIcon,
                    Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
            Object buttonDataInit = buttonDataModel.getClass().getDeclaredConstructors()[0]
                    .getParameterTypes()[0].newInstance();
            XposedHelpers.setObjectField(buttonDataInit, "A01", buttonIconData);
            XposedHelpers.setObjectField(buttonDataInit, "A04", buttonModel.getContentDescription());
            XposedHelpers.setObjectField(buttonDataInit, "A05", buttonModel.getAction());
            Object buttonData = XposedHelpers.newInstance(buttonDataModel.getClass(), buttonDataInit);
            inputButtons.add(buttonData);
        }
    }

    public TitleBarButtonsExtender addButton(TitleBarButtonModel buttonModel) {
        mButtonModels.add(buttonModel);
        return this;
    }

    public static class TitleBarButtonModel {
        private final @DrawableRes int mIcon;
        private final String mContentDescription;
        private final String mAction;

        public TitleBarButtonModel(@DrawableRes int icon, String contentDescription, String action) {
            mIcon = icon;
            mContentDescription = contentDescription;
            mAction = action;
        }

        public BitmapDrawable getIcon() { return (BitmapDrawable) ResourcesCompat.getDrawable(MProMain.getMProResources(), mIcon, null); }
        public String getContentDescription() { return mContentDescription; }
        public String getAction() { return mAction; }
    }
}
