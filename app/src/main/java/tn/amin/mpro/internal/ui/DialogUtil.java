package tn.amin.mpro.internal.ui;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;

public class DialogUtil {
    public static Object getMigColorScheme() {
        return XposedHelpers.callStaticMethod(
                MProMain.getReflectedClasses().X_MUtilities,
                "A0T",
                XposedHelpers.getObjectField(MProMain.getContext(), "A00"),
                9318);
    }
}
