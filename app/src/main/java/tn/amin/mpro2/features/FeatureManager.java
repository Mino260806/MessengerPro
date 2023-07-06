package tn.amin.mpro2.features;

import java.util.LinkedHashMap;
import java.util.Map;

import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.HookManager;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class FeatureManager {
    private final HookManager mHookManager;

    // We use a LinkedHashMap to preserve order in toolbar
    private final Map<FeatureId, Feature> mFeatures = new LinkedHashMap<>();

    public FeatureManager(HookManager hookManager) {
        mHookManager = hookManager;
    }

    public void addFeature(Feature feature) {
        for (HookId hookId: feature.getHookIds()) {
            mHookManager.registerListener(hookId, feature);
        }
        mFeatures.put(feature.getId(), feature);
    }

    public Feature getFeature(FeatureId featureId) {
        return mFeatures.get(featureId);
    }

    public Feature[] getByCategory(ToolbarButtonCategory category) {
        return mFeatures.values()
                .stream()
                .filter(feature -> feature.getToolbarCategory() == category)
                .toArray(Feature[]::new);
    }
}
