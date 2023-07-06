package tn.amin.mpro2.hook;

import android.content.Context;
import android.content.Intent;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro2.debug.Logger;

public class BroadcastReceiverHook {
    public BroadcastReceiverHook(String broadcastReceiverName, ClassLoader classLoader,
                                 String actionParamName, BroadcastReceiverEventListener listener) {
        Class<?> TargetReceiver = XposedHelpers.findClass(broadcastReceiverName, classLoader);

        Method onReceive = XposedHelpers.findMethodBestMatch(TargetReceiver, "onReceive", Context.class, Intent.class);

        XposedBridge.hookMethod(onReceive, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Logger.info("onReceive ! " + param.thisObject.getClass().getName());
                if (TargetReceiver.isInstance(param.thisObject)) {
                    Context context = (Context) param.args[0];
                    Intent intent = (Intent) param.args[1];

                    if (intent.hasExtra(actionParamName)) {
                        String action = intent.getStringExtra(actionParamName);
                        listener.onReceiveBroadcast(context, intent, action);

                        param.setResult(null);
                    }
                }
            }
        });
    }

    public interface BroadcastReceiverEventListener {
        void onReceiveBroadcast(Context context, Intent intent, String action);
    }
}
