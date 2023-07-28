package tn.amin.mpro2.features.action;

import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.BACKGROUND_GRADIENT_COLORS;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.COMPOSER_BACKGROUND_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.COMPOSER_INPUT_BACKGROUND_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.COMPOSER_INPUT_PLACEHOLDER_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.COMPOSER_TINT_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.COMPOSER_UNSELECTED_TINT_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.DELIVERY_RECEIPT_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.FALLBACK_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.GRADIENT_COLORS;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.HOT_LIKE_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.INBOUND_MESSAGE_GRADIENT_COLORS;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.LARGE_BACKGROUND_IMAGE_LONG;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.LARGE_BACKGROUND_IMAGE_STRING1;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.LARGE_BACKGROUND_IMAGE_STRING2;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.PRIMARY_BUTTON_BACKGROUND_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.REACTION_PILL_BACKGROUND_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.TITLE_BAR_BACKGROUND_COLOR;
import static tn.amin.mpro2.orca.OrcaThreadThemeAttr.TITLE_BAR_BUTTON_TINT_COLOR;

import android.app.AlertDialog;
import android.graphics.Color;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.HashMap;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.theme.ColorSubstitute;
import tn.amin.mpro2.features.util.theme.ThemeConfigurationFrame;
import tn.amin.mpro2.features.util.theme.ThemeInfo;
import tn.amin.mpro2.features.util.theme.Themes;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.ConversationEnterHook;
import tn.amin.mpro2.hook.all.ConversationLeaveHook;
import tn.amin.mpro2.hook.all.ThreadAttrsHook;
import tn.amin.mpro2.hook.all.UIColorsHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaColors;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.OrcaThreadThemeAttr;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class ColorThemingFeature extends Feature
        implements UIColorsHook.OnUiColorsListener,
        ThreadAttrsHook.OnThreadAttrListener,
                   ConversationEnterHook.ConversationEnterListener,
                   ConversationLeaveHook.ConversationLeaveListener {
    private static final HashMap<OrcaThreadThemeAttr, Pair<?, ?>> mThreadThemeAttrMap = new HashMap<>();

    static {
        mThreadThemeAttrMap.put(BACKGROUND_GRADIENT_COLORS, OrcaColors.SURFACE.toStringPair());
        mThreadThemeAttrMap.put(COMPOSER_BACKGROUND_COLOR, OrcaColors.SURFACE.toIntPair());
        mThreadThemeAttrMap.put(TITLE_BAR_BACKGROUND_COLOR, OrcaColors.SURFACE.toIntPair());

        mThreadThemeAttrMap.put(LARGE_BACKGROUND_IMAGE_LONG, new Pair<>(null, null));
        mThreadThemeAttrMap.put(LARGE_BACKGROUND_IMAGE_STRING1, new Pair<>(null, null));
        mThreadThemeAttrMap.put(LARGE_BACKGROUND_IMAGE_STRING2, new Pair<>(null, null));

        mThreadThemeAttrMap.put(GRADIENT_COLORS, OrcaColors.PRIMARY.toStringPair());
        mThreadThemeAttrMap.put(TITLE_BAR_BUTTON_TINT_COLOR, OrcaColors.PRIMARY.toIntPair());
        mThreadThemeAttrMap.put(PRIMARY_BUTTON_BACKGROUND_COLOR, OrcaColors.PRIMARY.toIntPair());
        mThreadThemeAttrMap.put(COMPOSER_TINT_COLOR, OrcaColors.PRIMARY.toIntPair());
        mThreadThemeAttrMap.put(HOT_LIKE_COLOR, OrcaColors.PRIMARY.toIntPair());
        mThreadThemeAttrMap.put(FALLBACK_COLOR, OrcaColors.PRIMARY.toIntPair());

        mThreadThemeAttrMap.put(COMPOSER_INPUT_BACKGROUND_COLOR, OrcaColors.EDITTEXT_INPUT_BACKGROUND.toIntPair());

        mThreadThemeAttrMap.put(INBOUND_MESSAGE_GRADIENT_COLORS, OrcaColors.SURFACE_VARIANT_3.toStringPair());
        mThreadThemeAttrMap.put(REACTION_PILL_BACKGROUND_COLOR, OrcaColors.SURFACE_VARIANT_3.toIntPair());

        mThreadThemeAttrMap.put(COMPOSER_INPUT_PLACEHOLDER_COLOR, OrcaColors.EDITTEXT_INPUT_PLACEHOLDER_COLOR.toIntPair());

        mThreadThemeAttrMap.put(DELIVERY_RECEIPT_COLOR, OrcaColors.PRIMARY_VARIANT_2.toIntPair());

        mThreadThemeAttrMap.put(COMPOSER_UNSELECTED_TINT_COLOR, OrcaColors.PRIMARY_DIMMED.toIntPair());
    }

    private int mThemeIndex;
    private ThemeInfo mTheme;
    private final ColorSubstitute mColorSubstitute = new ColorSubstitute();

    private boolean canApply = true;

    public ColorThemingFeature(OrcaGateway gateway) {
        super(gateway);

        Themes.addMonetThemeIfSupported(gateway.getContext().getResources());

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
        return new HookId[] { HookId.UI_COLORS, HookId.THREAD_ATTRS, HookId.CONVERSATION_ENTER, HookId.CONVERSATION_LEAVE };
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

    public void setTheme(int themeIndex) {
        mTheme = Themes.themes.get(themeIndex);
        mThemeIndex = themeIndex;
        mColorSubstitute.setTheme(mTheme);
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

        Integer replacement = mColorSubstitute.substitute(color);
        if (replacement == null) return HookListenerResult.ignore();

        return HookListenerResult.consume(replacement);
    }

    @Override
    public void onNavBarColorSet(int color) {
        if (color == Color.BLACK) mColorSubstitute.setLightMode(false);
        else if (color == Color.WHITE) mColorSubstitute.setLightMode(true);
    }

    @Override
    public HookListenerResult<?> onThreadAttrQuery(OrcaThreadThemeAttr attr, int themeIndex) {
        if (canApply && mThreadThemeAttrMap.containsKey(attr)) {
            Object result = mColorSubstitute.getLightMode()?
                    mThreadThemeAttrMap.get(attr).first:
                    mThreadThemeAttrMap.get(attr).second;
            return HookListenerResult.consume(result);
        }

        return HookListenerResult.ignore();
    }

    @Override
    public void onConversationEnter(Long threadKey) {
        if (!gateway.pref.getColorThemeForce())
            canApply = false;
    }

    @Override
    public void onConversationLeave() {
        canApply = true;
    }
}
