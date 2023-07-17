package tn.amin.mpro2.ui;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.StringRes;

public class Toaster {
    private final Context context;
    private final ModuleResources res;

    public Toaster(Context context, ModuleResources res) {
        this.context = context;
        this.res = res;
    }

    public void toast(@StringRes int message, Object[] args, boolean isLongDuration) {
        Toast.makeText(context, res.getString(message, args),
                isLongDuration? Toast.LENGTH_LONG: Toast.LENGTH_SHORT).show();
    }
}
