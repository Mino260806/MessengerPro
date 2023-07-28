package tn.amin.mpro2.orca.delegate;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Function;

public class CQLResultSetDelegateFactory {
    public static Object getDelegate(Object child, Class<?> CQLResultSet,
                                     CQLResultReplacer replacer) {
        return Proxy.newProxyInstance(
                CQLResultSet.getClassLoader(),
                new Class<?>[]{CQLResultSet},
                (proxy, method, args) -> {
                    String methodName = method.getName();
                    if (methodName.startsWith("get")) {
                        int i1 = (int) args[0];
                        int i2 = (int) args[1];
                        if (replacer.shouldReplace(i1, i2)) {
                            return replacer.replace(i1, i2);
                        }
                    }

                    return getOriginalResult(child, method, args);
                }
        );
    }

    private static Object getOriginalResult(Object child, Method method, Object[] args) {
        try {
            return method.invoke(child, args);
        } catch (Throwable ignored) {
            return null;
        }
    }

    public interface CQLResultReplacer {
        boolean shouldReplace(int attrCategory, int attrId);
        Object replace(int attrCategory, int attrId);
    }
}
