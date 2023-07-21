package tn.amin.mpro2.hook.all;

import android.graphics.Color;
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
        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "setColor", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                final long color;
                final boolean isLong;
                if (param.args[0] instanceof Long) {
                    isLong = true;
                    color = (long) param.args[0];
                }
                else if (param.args[0] instanceof Integer) {
                    isLong = false;
                    color = (int) param.args[0];
                }
                else {
                    isLong = false;
                    color = 0;
                }
                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
                    if (isLong) param.args[0] = getListenersReturnValue().value;
                    else param.args[0] = ((Long) getListenersReturnValue().value).intValue();
                }
            }
        }));

//        unhooks.addAll(XposedBridge.hookAllConstructors(PorterDuffColorFilter.class, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                final long color = (int) param.args[0];
//
//                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
//                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
//                    param.args[0] = ((Long) getListenersReturnValue().value).intValue();
//                }
//            }
//        }));

        unhooks.addAll(XposedBridge.hookAllMethods(Paint.class, "setColorFilter", new XC_MethodHook() {
            HashMap<Long, PorterDuffColorFilter> filters = new HashMap<>();

            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] instanceof PorterDuffColorFilter) {
                    int color = (int) XposedHelpers.callMethod(param.args[0], "getColor");
                    PorterDuff.Mode mode = (PorterDuff.Mode) XposedHelpers.callMethod(param.args[0], "getMode");

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
                            param.args[0] = replacement;
                        }
                    }
                }
            }
        }));

        unhooks.addAll(XposedBridge.hookAllMethods(View.class, "setBackgroundColor", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                final long color = (int) param.args[0];
                notifyListenersWithResult((listener) -> ((OnUiColorsListener) listener).onColorPreDraw(color));
                if (getListenersReturnValue() != null && getListenersReturnValue().value != null) {
                    param.args[0] = ((Long) getListenersReturnValue().value).intValue();
                }
            }
        }));

        unhooks.addAll(XposedBridge.hookAllMethods(XposedHelpers.findClass("com.android.internal.policy.PhoneWindow", gateway.classLoader),
                "setNavigationBarColor", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        final long color = (int) param.args[0];
                        notifyListeners((listener) -> ((OnUiColorsListener) listener).onNavBarColorSet(color));
                    }
                }));

        return unhooks;
    }

    public interface OnUiColorsListener {
        HookListenerResult<Long> onColorPreDraw(@ColorLong long color);
        void onNavBarColorSet(@ColorLong long color);
    }
}
