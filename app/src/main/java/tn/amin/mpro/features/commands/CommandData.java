package tn.amin.mpro.features.commands;

import android.view.View;

import com.mojang.brigadier.Command;

import de.robv.android.xposed.XposedHelpers;
import tn.amin.mpro.MProMain;
import tn.amin.mpro.constants.Constants;

public class CommandData {
    public static Object newInstance(CommandFields cf) {
        final String title = cf.name;
        final String description = cf.description;
        final String iconName = cf.getIconName();

        Class<?> X_CommandInterface = MProMain.getReflectedClasses().X_CommandInterface;

        Object thisObject = java.lang.reflect.Proxy.newProxyInstance(
                X_CommandInterface.getClassLoader(),
                new java.lang.Class[] { X_CommandInterface },
                (o, method, objects) -> {
                    String methodName = method.getName();
                    switch (methodName) {
                        case "Af4":
                        case "B3J": {
                            return title;
                        }
                        case "B1B": {
                            return description;
                        }

                        case "B4K": {
                            return Constants.MPRO_MENTIONS_AUTOCOMPLETE_TYPE;
                        }

                        case "AtU": {
                            return iconName;
                        }
                        case "equals": {
                            return false;
                        }
                        case "hashCode": {
                            throw new UnsupportedOperationException("hashCode method of CommandData not implemented");
                        }
                        case "Aax":
                        case "B57":
                        case "getId":
                        default: {
                            return null;
                        }
                    }
                });
        return thisObject;
    }

    public static class OnClickListener implements View.OnClickListener {
        private final Object mMentionData;
        public OnClickListener(Object mentionData) {
            mMentionData = mentionData;
        }

        @Override
        public void onClick(View view) {
            MProMain.showCommandsAutoComplete(null);
            String commandLiteral = "/" + XposedHelpers.callMethod(mMentionData, "B3J");
            MProMain.getActiveMessageEdit().setText(commandLiteral);
            MProMain.getActiveMessageEdit().setSelection(commandLiteral.length());
        }
    }
}
