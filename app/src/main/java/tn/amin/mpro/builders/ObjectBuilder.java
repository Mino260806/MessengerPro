package tn.amin.mpro.builders;

import de.robv.android.xposed.XposedHelpers;

abstract public class ObjectBuilder {
    protected void setFieldInternal(String fieldName, Object value) {
        XposedHelpers.setObjectField(getWrapper(), fieldName, value);
    }

    abstract protected Object getWrapper();
    abstract public Object build();
}
