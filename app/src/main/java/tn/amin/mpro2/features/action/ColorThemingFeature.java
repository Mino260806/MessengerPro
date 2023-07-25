package tn.amin.mpro2.features.action;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import tn.amin.mpro2.R;
import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.theme.ColorsThemeReplacer;
import tn.amin.mpro2.features.util.theme.ThemeConfigurationFrame;
import tn.amin.mpro2.features.util.theme.ThemeInfo;
import tn.amin.mpro2.features.util.theme.Themes;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.ConversationEnterHook;
import tn.amin.mpro2.hook.all.ConversationLeaveHook;
import tn.amin.mpro2.hook.all.UIColorsHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class ColorThemingFeature extends Feature
        implements UIColorsHook.OnUiColorsListener, ConversationEnterHook.ConversationEnterListener, ConversationLeaveHook.ConversationLeaveListener {
    private int mThemeIndex;
    private ThemeInfo mTheme;
    private final ColorsThemeReplacer mThemeReplacer = new ColorsThemeReplacer();

    private boolean canApply = true;

    public ColorThemingFeature(OrcaGateway gateway) {
        super(gateway);

        if (isEnabled()) {
            gateway.doOnActivity(() -> new Handler(Looper.getMainLooper()).post(
                    () -> gateway.getActivity().recreate()));
        }

        setTheme(gateway.pref.getColorTheme());
    }

    @Override
    public FeatureId getId() {
        return FeatureId.COLOR_THEMING;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.ACTION;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.UI_COLORS, HookId.CONVERSATION_ENTER, HookId.CONVERSATION_LEAVE };
    }

    @Override
    public boolean isEnabledByDefault() {
        return false;
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_ui_color_theme_enable";
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.QUICK_ACTION;
    }

    @Nullable
    @Override
    public Integer getToolbarDescription() {
        return R.string.feature_color_theme;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_theme;
    }

    @Override
    public void executeAction() {
        if (!isEnabled()) {
            Toast.makeText(gateway.getActivity(), gateway.res.getString(R.string.please_enable_theme), Toast.LENGTH_SHORT).show();
            return;
        }

        ThemeConfigurationFrame configurationFrame = new ThemeConfigurationFrame(
                gateway.getActivityWithModuleResources(), gateway, mThemeIndex);

        AlertDialog dialog = new AlertDialog.Builder(gateway.getActivity())
                .setTitle(gateway.res.getString(R.string.theme))
                .setView(configurationFrame)
                .show();

        configurationFrame.setOnApplyListener(themeIndex -> {
            setTheme(themeIndex);
            dialog.dismiss();
            gateway.getActivity().recreate();
        });
    }

    @Override
    public HookListenerResult<Integer> onColorPreDraw(int color) {
        if (!canApply) return HookListenerResult.ignore();

        Integer replacement = mThemeReplacer.replaceColor(color);
        if (replacement == null) return HookListenerResult.ignore();

        return HookListenerResult.consume(replacement);
    }

    @Override
    public void onNavBarColorSet(int color) {
        boolean isLightMode = true;
        if (color == Color.BLACK) isLightMode = false;
        else if (color != Color.WHITE) Logger.warn("Unknown nav bar color, defaulting to light mode");

        Logger.verbose("Light mode: " + isLightMode);
        mThemeReplacer.setLightMode(isLightMode);
    }

    public void setTheme(int themeIndex) {
        mTheme = Themes.themes[themeIndex];
        mThemeIndex = themeIndex;
        mThemeReplacer.setTheme(mTheme);
    }

    @Override
    public void onConversationEnter(Long threadKey) {
        canApply = false;
    }

    @Override
    public void onConversationLeave() {
        canApply = true;
    }
}
