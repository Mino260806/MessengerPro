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
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.commons.lang3.StringUtils;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.util.theme.supplier.CustomThemeColorSupplier;
import tn.amin.mpro2.orca.OrcaGateway;

public class ThemeConfigurationFrame extends FrameLayout {
    private OnApplyListener mListener;

    private final Spinner mSpinner;

    private final EditText mSeedEdit;

    private int mThemeIndex;

    public ThemeConfigurationFrame(@NonNull Context context, OrcaGateway gateway, int themeIndex) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.layout_theme_config, this);

        mSpinner = findViewById(R.id.theme_spinner);
        mSeedEdit = findViewById(R.id.theme_seed_edit);
        Button applyButton = findViewById(R.id.button_apply);

        applyButton.setText(gateway.res.getString(R.string.apply));

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
            ThemeInfo themeInfo = Themes.themes.get(mThemeIndex);
            if (themeInfo.colorSupplier instanceof CustomThemeColorSupplier) {
                String colorInput = mSeedEdit.getText().toString();
                int color;
                if (StringUtils.isBlank(colorInput)) {
                    mSeedEdit.setError(gateway.res.getText(R.string.invalid_color));
                    return;
                }
                try {
                    color = Color.parseColor(colorInput);
                } catch (IllegalArgumentException e) {
                    Logger.error(e);
                    mSeedEdit.setError(gateway.res.getText(R.string.invalid_color));
                    return;
                }

                ((CustomThemeColorSupplier) themeInfo.colorSupplier).setSeedColor(color);
            }

            gateway.pref.setColorTheme(mThemeIndex);

            if (mListener != null) mListener.onApply(mThemeIndex);
        });

        setTheme(themeIndex, false);
    }

    private void setTheme(int themeIndex, boolean fromUser) {
        mThemeIndex = themeIndex;
        ThemeInfo themeInfo = Themes.themes.get(themeIndex);
        if (!fromUser)
            mSpinner.setSelection(themeIndex);
        mSeedEdit.setText(colorToInput(themeInfo.getSeedColor()));
        mSeedEdit.setEnabled(themeInfo.colorSupplier instanceof CustomThemeColorSupplier);
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
