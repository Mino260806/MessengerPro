package tn.amin.mpro2.hook.all;

import android.graphics.BlendMode;
import android.graphics.BlendModeColorFilter;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.util.Pair;
import android.widget.ImageView;

import androidx.annotation.ColorInt;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;

public class UIColorsHook extends BaseHook {
    @Override
    public HookId getId() {
        return HookId.UI_COLORS;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        Set<XC_MethodHook.Unhook> unhooks = new HashSet<>();

        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "setColorFilter", wrap(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ColorFilter newColorFilter = replaceColorFilter((ColorFilter) param.args[0]);
                if (newColorFilter != null)
                    param.args[0] = newColorFilter;
            }
        })));

        unhooks.addAll(XposedBridge.hookAllMethods(PorterDuffColorFilter.class, "createNativeInstance", wrap(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                ColorFilter newColorFilter = replaceColorFilter((ColorFilter) param.thisObject);
                if (newColorFilter != null)
                    param.setResult(XposedHelpers.callMethod(newColorFilter, "createNativeInstance"));
            }
        })));

        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "getNativeInstance", wrap(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Paint paint = (Paint) param.thisObject;

                Integer replacedColor = replaceColor(paint.getColor());
                if (replacedColor != null) paint.setColor(replacedColor);
            }
        })));

        unhooks.addAll(XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.internal.policy.PhoneWindow", gateway.classLoader),
                "setNavigationBarColor", wrap(new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        final int color = (int) param.args[0];
                        notifyListeners((listener) -> ((OnUiColorsListener) listener).onNavBarColorSet(color));
                    }
                })));

        return unhooks;
    }

    public interface OnUiColorsListener {
        HookListenerResult<Integer> onColorPreDraw(@ColorInt int color);
        void onNavBarColorSet(@ColorInt int color);
    }

    private HashMap<Pair<Integer, PorterDuff.Mode>, PorterDuffColorFilter> porterDuffFilters = new HashMap<>();
    private HashMap<Pair<Integer, BlendMode>, BlendModeColorFilter> blendModeFilters = new HashMap<>();
    public ColorFilter replaceColorFilter(ColorFilter colorFilter) {
        if (colorFilter instanceof PorterDuffColorFilter) {
            int color = (int) XposedHelpers.callMethod(colorFilter, "getColor");
            PorterDuff.Mode mode = (PorterDuff.Mode) XposedHelpers.callMethod(colorFilter, "getMode");

            if (mode == PorterDuff.Mode.SRC_IN || mode == PorterDuff.Mode.SRC_ATOP) {
                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
                    int newColor = (Integer) getListenersReturnValue().value;
                    PorterDuffColorFilter replacement;
                    Pair<Integer, PorterDuff.Mode> attrPair = new Pair<>(newColor, mode);
                    if (porterDuffFilters.containsKey(attrPair)) {
                        replacement = porterDuffFilters.get(attrPair);
                    } else {
                        replacement = new PorterDuffColorFilter(newColor, PorterDuff.Mode.SRC_IN);
                        porterDuffFilters.put(attrPair, replacement);
                    }
                    return replacement;
                }
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                && colorFilter instanceof BlendModeColorFilter) {
            BlendModeColorFilter blendModeColorFilter = (BlendModeColorFilter) colorFilter;
            int color = blendModeColorFilter.getColor();
            BlendMode mode = blendModeColorFilter.getMode();

            if (mode == BlendMode.SRC_IN || mode == BlendMode.SRC_ATOP) {
                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
                    int newColor = (Integer) getListenersReturnValue().value;
                    BlendModeColorFilter replacement;
                    Pair<Integer, BlendMode> attrPair = new Pair<>(newColor, mode);
                    if (blendModeFilters.containsKey(attrPair)) {
                        replacement = blendModeFilters.get(attrPair);
                    } else {
                        replacement = new BlendModeColorFilter(newColor, mode);
                        blendModeFilters.put(attrPair, replacement);
                    }
                    return replacement;
                }
            }
        }

        return null;
    }

    public Integer replaceColor(Integer color) {
        notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
        if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
            return (Integer) getListenersReturnValue().value;
        }
        return null;
    }
}
