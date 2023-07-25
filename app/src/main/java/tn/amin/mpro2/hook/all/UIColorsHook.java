package tn.amin.mpro2.hook.all;

import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.ColorLong;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
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
//        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "setColor", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Long newColor = replaceColor(((Number) param.args[0]).longValue());
//                if (newColor != null) {
//                    if (param.args[0] instanceof Long) param.args[0] = newColor;
//                    else param.args[0] = newColor.intValue();
//                }
//            }
//        }));

//        unhooks.addAll(XposedBridge.hookAllConstructors(PorterDuffColorFilter.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Integer replacedColor = replaceColor((Integer) param.args[0]);
//                if (replacedColor != null) param.args[0] = replacedColor;
//            }
//        }));

//        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "setColorFilter", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                ColorFilter newColorFilter = replaceColorFilter((ColorFilter) param.args[0]);
//                if (newColorFilter != null)
//                    param.args[0] = newColorFilter;
//            }
//        }));

//        unhooks.addAll(XposedBridge.hookAllMethods(View.class, "setBackgroundColor", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                final long color = (int) param.args[0];
//                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
//                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
//                    param.args[0] = ((Long) getListenersReturnValue().value).intValue();
//                }
//            }
//        }));

        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "getNativeInstance", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Paint paint = (Paint) param.thisObject;

                Integer replacedColor = replaceColor(paint.getColor());
                if (replacedColor != null) paint.setColor(replacedColor);
            }
        }));

        unhooks.addAll(XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.internal.policy.PhoneWindow", gateway.classLoader),
                "setNavigationBarColor", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        final int color = (int) param.args[0];
                        notifyListeners((listener) -> ((OnUiColorsListener) listener).onNavBarColorSet(color));
                    }
                }));

        return unhooks;
    }

    public interface OnUiColorsListener {
        HookListenerResult<Integer> onColorPreDraw(@ColorLong int color);
        void onNavBarColorSet(@ColorLong int color);
    }

    HashMap<Long, PorterDuffColorFilter> filters = new HashMap<>();
    public ColorFilter replaceColorFilter(ColorFilter colorFilter) {
        if (colorFilter instanceof PorterDuffColorFilter) {
            int color = (int) XposedHelpers.callMethod(colorFilter, "getColor");
            PorterDuff.Mode mode = (PorterDuff.Mode) XposedHelpers.callMethod(colorFilter, "getMode");

            if (mode == PorterDuff.Mode.SRC_IN) {
                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
                    Long newColor = (Long) getListenersReturnValue().value;
                    PorterDuffColorFilter replacement;
                    if (filters.containsKey(newColor)) {
                        replacement = filters.get(newColor);
                    } else {
                        replacement = new PorterDuffColorFilter(newColor.intValue(), PorterDuff.Mode.SRC_IN);
                        filters.put(newColor, replacement);
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
