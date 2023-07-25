package tn.amin.mpro2.features.util.theme;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.util.theme.supplier.StaticThemeColorSupplier;
import tn.amin.mpro2.orca.OrcaGateway;

public class ThemeConfigurationFrame extends FrameLayout {
    private OnApplyListener mListener;

    private final Spinner mSpinner;
    private final HashMap<ColorType, EditText> mColorEdits = new HashMap<>();

    private int mThemeIndex;
    private ThemeInfo mThemeInfo;

    public ThemeConfigurationFrame(@NonNull Context context, OrcaGateway gateway, int themeIndex) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.layout_theme_config, this);

        mSpinner = findViewById(R.id.theme_spinner);
        GridLayout layout = findViewById(R.id.theme_layout);
        Button applyButton = findViewById(R.id.button_apply);

        applyButton.setText(gateway.res.getString(R.string.apply));

        for (ColorType colorType: ColorType.values()) {
            TextView label = new TextView(context);
            label.setText(colorType.name());

            EditText colorEdit = new EditText(context);
            colorEdit.setHint("unchanged");
            mColorEdits.put(colorType, colorEdit);

            GridLayout.LayoutParams layoutParams;

            layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.rowSpec = GridLayout.spec(colorType.ordinal());
            layoutParams.columnSpec = GridLayout.spec(0);
            layout.addView(label, layoutParams);

            layoutParams = new GridLayout.LayoutParams();
            layoutParams.width = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = GridLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.rowSpec = GridLayout.spec(colorType.ordinal());
            layoutParams.columnSpec = GridLayout.spec(1);
            layout.addView(colorEdit, layoutParams);
        }

        mSpinner.setAdapter(new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, Themes.getThemeNames()));
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                setTheme(position, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        applyButton.setOnClickListener((b) -> {
            if (mThemeInfo.name.equals("Custom")) {
                StaticThemeColorSupplier.Builder colorSupplierBuilder = new StaticThemeColorSupplier.Builder();
                for (Map.Entry<ColorType, EditText> colorEditEntry: mColorEdits.entrySet()) {
                    String colorInput = colorEditEntry.getValue().getText().toString().trim();
                    if (StringUtils.isBlank(colorInput)) continue;
                    int color;
                    try {
                        color = Color.parseColor(colorInput);
                    } catch (IllegalArgumentException e) {
                        Logger.error(e);
                        colorEditEntry.getValue().setError(gateway.res.getString(R.string.invalid_color));
                        return;
                    }
                    colorSupplierBuilder.addColor(colorEditEntry.getKey(), color);

                }
                mThemeInfo.colorSupplier = colorSupplierBuilder.build();
            }

            gateway.pref.setColorTheme(mThemeIndex);

            if (mListener != null) mListener.onApply(mThemeIndex);
        });

        setTheme(themeIndex, false);
    }

    private void setTheme(int themeIndex, boolean fromUser) {
        mThemeIndex = themeIndex;
        mThemeInfo = Themes.themes.get(themeIndex);
        if (!fromUser)
            mSpinner.setSelection(themeIndex);
        for (Map.Entry<ColorType, EditText> colorEditEntry: mColorEdits.entrySet()) {
            if (mThemeInfo.colorSupplier == null) {
                colorEditEntry.getValue().setText("");
                colorEditEntry.getValue().setEnabled(true);
                continue;
            }

            Integer color = mThemeInfo.colorSupplier.getColor(colorEditEntry.getKey());
            colorEditEntry.getValue().setText(colorToInput(color));
            colorEditEntry.getValue().setEnabled(mThemeInfo.name.equals("Custom"));
        }
    }

    private String colorToInput(@Nullable Integer color) {
        if (color != null) {
            return String.format("#%06X", (0xFFFFFF & color));
        } else {
            return "";
        }
    }

    public void setOnApplyListener(OnApplyListener listener) {
        mListener = listener;
    }

    public interface OnApplyListener {
        void onApply(int themeIndex);
    }
}
