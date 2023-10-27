package tn.amin.mpro2.hook.all;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.HookTime;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.hook.unobfuscation.OrcaUnobfuscator;
import tn.amin.mpro2.orca.OrcaGateway;

public class TypingIndicatorSentHook extends BaseHook {
    @Override
    public HookId getId() {
        return HookId.TYPING_INDICATOR_SEND;
    }

    @Override
    public HookTime getHookTime() {
        return HookTime.AFTER_DEOBFUSCATION;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        Class<?> TypingIndicatorDispatcher = gateway.unobfuscator.getClass(OrcaUnobfuscator.CLASS_TYPING_INDICATOR_DISPATCHER);

        if (TypingIndicatorDispatcher == null)
            throw new RuntimeException(OrcaUnobfuscator.CLASS_TYPING_INDICATOR_DISPATCHER + " is null");

        Method dispatchTypingIndicator = TypingIndicatorDispatcher.getMethods()[0];
        return Collections.singleton(XposedBridge.hookMethod(dispatchTypingIndicator, wrap(new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                notifyListenersWithResult((listener) -> ((TypingIndicatorSentListener) listener).onTypingIndicatorSent());
                boolean allowTypingIndicator = !getListenersReturnValue().isConsumed || (Boolean) getListenersReturnValue().value;
                if (!allowTypingIndicator) {
                    param.setResult(null);
                }
            }
        })));
    }

    public interface TypingIndicatorSentListener {
        HookListenerResult<Boolean> onTypingIndicatorSent();
    }
}
