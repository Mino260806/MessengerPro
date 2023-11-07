package tn.amin.mpro2.debug.methodhook;

import java.lang.reflect.Field;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import tn.amin.mpro2.debug.Logger;

public class MethodHookLogParams extends XC_MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);

        Logger.verbose(param.method + " called ! " + param.args.length + " arguments");;
        Logger.verbose(".S......................................................");
        for (int i = 0; i < param.args.length; i++) {
            Object arg = param.args[i];
            if (arg == null) {
                Logger.verbose(i + "-null");
            } else {
                if (arg instanceof ArrayList) {
                    ArrayList<?> arrayList = (ArrayList<?>) arg;
                    Logger.verbose(i + "-ArrayList: " + arrayList.toString());
                } else {
                    Logger.verbose(i + "-" + arg.getClass().getName() + ":" + arg);
                }
            }
        }
//        if (param.method.toString().contains("getIsUnsent")) {
//            param.setResult(false);
//        }
        Logger.verbose(".E......................................................");
    }
}
