package tn.amin.mpro2.ui;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.view.LayoutInflater;

public class ModuleContextWrapper extends ContextWrapper {
    private final Resources mResources;
    private LayoutInflater mInflater = null;

    public ModuleContextWrapper(Context base, Resources resources) {
        super(base);

        mResources = resources;
    }

    @Override
    public Resources getResources() {
        return mResources;
    }

    /**
     * Required so that the LayoutInflater uses this context to resolve resources.
     * <a href="https://github.com/slightfoot/android-edge-effect-override/blob/master/EdgeEffectOverride.java">Credits</a>
     */
    @Override
    public Object getSystemService(String name)
    {
        if(LAYOUT_INFLATER_SERVICE.equals(name)){
            if(mInflater == null){
                mInflater = LayoutInflater.from(getBaseContext()).cloneInContext(this);
            }
            return mInflater;
        }

        return super.getSystemService(name);
    }
}
