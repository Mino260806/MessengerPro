package tn.amin.mpro2.hook.all;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.constants.OrcaClassNames;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.HookTime;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.OrcaThreadThemeAttr;
import tn.amin.mpro2.orca.delegate.CQLResultSetDelegateFactory;

public class ThreadAttrsHook extends BaseHook
        implements CQLResultSetDelegateFactory.CQLResultReplacer {
    private static final HashMap<Integer, OrcaThreadThemeAttr> mMap = new HashMap<>();

    static {
        mMap.put(16, OrcaThreadThemeAttr.BACKGROUND_GRADIENT_COLORS);
        mMap.put(31, OrcaThreadThemeAttr.COMPOSER_BACKGROUND_COLOR);
        mMap.put(28, OrcaThreadThemeAttr.COMPOSER_INPUT_BACKGROUND_COLOR);
        mMap.put(27, OrcaThreadThemeAttr.COMPOSER_INPUT_PLACEHOLDER_COLOR);
        mMap.put(25, OrcaThreadThemeAttr.COMPOSER_TINT_COLOR);
        mMap.put(26, OrcaThreadThemeAttr.COMPOSER_UNSELECTED_TINT_COLOR);
        mMap.put(10, OrcaThreadThemeAttr.DELIVERY_RECEIPT_COLOR);
        mMap.put(4,  OrcaThreadThemeAttr.FALLBACK_COLOR);
        mMap.put(34, OrcaThreadThemeAttr.GRADIENT_COLORS);
        mMap.put(40, OrcaThreadThemeAttr.HOT_LIKE_COLOR);
        mMap.put(41, OrcaThreadThemeAttr.INBOUND_MESSAGE_GRADIENT_COLORS);
        mMap.put(17, OrcaThreadThemeAttr.LARGE_BACKGROUND_IMAGE_STRING1);
        mMap.put(18, OrcaThreadThemeAttr.LARGE_BACKGROUND_IMAGE_STRING2);
        mMap.put(19, OrcaThreadThemeAttr.LARGE_BACKGROUND_IMAGE_LONG);
        mMap.put(35, OrcaThreadThemeAttr.MESSAGE_TEXT_COLOR);
        mMap.put(32, OrcaThreadThemeAttr.PRIMARY_BUTTON_BACKGROUND_COLOR);
        mMap.put(47, OrcaThreadThemeAttr.REACTION_PILL_BACKGROUND_COLOR);
        mMap.put(9,  OrcaThreadThemeAttr.TERTIARY_TEXT_COLOR);
        mMap.put(21, OrcaThreadThemeAttr.TITLE_BAR_BACKGROUND_COLOR);
        mMap.put(22, OrcaThreadThemeAttr.TITLE_BAR_BUTTON_TINT_COLOR);
    }

    @Override
    public HookId getId() {
        return HookId.THREAD_ATTRS;
    }

    @Override
    public HookTime getHookTime() {
        return HookTime.AFTER_DEOBFUSCATION;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return Collections.singleton(XposedBridge.hookMethod(gateway.unobfuscator.getMethod(OrcaUnobfuscator.METHOD_THREAD_THEME_INFO_FACTORY_CREATE), new XC_MethodHook() {
            Object originalResultSet = null;
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                originalResultSet = XposedHelpers.getObjectField(param.args[1], "mResultSet");
                XposedHelpers.setObjectField(param.args[1], "mResultSet",
                        CQLResultSetDelegateFactory.getDelegate(
                                originalResultSet,
                                XposedHelpers.findClass(OrcaClassNames.CQL_RESULT_SET, gateway.classLoader),
                                ThreadAttrsHook.this));
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                XposedHelpers.setObjectField(param.args[1], "mResultSet",
                        originalResultSet);
            }
        }));
    }

    @Override
    public boolean shouldReplace(int themeIndex, int attrId) {
        if (mMap.containsKey(attrId)) {
            OrcaThreadThemeAttr attr = mMap.get(attrId);
            notifyListenersWithResult(listener -> ((OnThreadAttrListener) listener).onThreadAttrQuery(attr, themeIndex));
            return getListenersReturnValue().isConsumed;
        }

        return false;
    }

    @Override
    public Object replace(int attrCategory, int attrId) {
        return getListenersReturnValue().value;
    }

    public interface OnThreadAttrListener {
        HookListenerResult<?> onThreadAttrQuery(OrcaThreadThemeAttr attr, int themeIndex);
    }
}
