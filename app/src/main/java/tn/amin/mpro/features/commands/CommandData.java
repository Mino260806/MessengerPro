package tn.amin.mpro.features.commands;

import tn.amin.mpro.MProMain;

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
                            return -1;
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

//    private static String getDescriptionForCommand() {
//
//    }
}
