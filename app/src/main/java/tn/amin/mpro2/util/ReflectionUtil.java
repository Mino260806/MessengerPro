package tn.amin.mpro2.util;

import android.view.View;

import java.lang.reflect.Field;

import tn.amin.mpro2.debug.Logger;

public class ReflectionUtil {
    public static View.OnTouchListener getOnTouchListenerFromView(View view) {
        try {
            Field listenerInfoField = View.class.getDeclaredField("mListenerInfo");
            listenerInfoField.setAccessible(true);
            Object listenerInfo = listenerInfoField.get(view);

            Field onTouchListenerField = listenerInfo.getClass().getDeclaredField("mOnTouchListener");
            onTouchListenerField.setAccessible(true);

            return (View.OnTouchListener) onTouchListenerField.get(listenerInfo);
        } catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            Logger.error(e);
        }

        return null;
    }
}
