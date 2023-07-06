package tn.amin.mpro2.preference;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class SettingsPanel extends LinearLayout {
    public SettingsPanel(Context context) {
        super(context);
        init();
    }

    public SettingsPanel(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SettingsPanel(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public SettingsPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        ScrollView scrollView = new ScrollView(getContext());
        addView(scrollView, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
        LinearLayout settingsContainer = new LinearLayout(getContext());
        settingsContainer.setOrientation(LinearLayout.VERTICAL);
        addView(settingsContainer);

        for (int i=0; i<20; i++) {
            TextView textView = new TextView(getContext());
            textView.setText("H\nEL\nLO");
            textView.setTextColor(Color.WHITE);
            textView.setBackgroundColor(Color.BLUE);
            settingsContainer.addView(textView);

            textView.setOnClickListener((v) -> {
                Toast.makeText(v.getContext(), "Click", Toast.LENGTH_SHORT).show();
            });
        }

    }

    public static void summon(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_PANEL,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_DIM_BEHIND ,
                PixelFormat.TRANSLUCENT);

        layoutParams.dimAmount = 0.7f;
        layoutParams.height = 500;

        SettingsPanel panel = new SettingsPanel(context);
        windowManager.addView(panel, layoutParams);
    }
}
