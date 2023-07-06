package tn.amin.mpro2.features.tasker;

import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.TypingIndicatorReceivedHook;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.tasker.ActivityOnUserTypingConfig;

public class TaskerEventTypingIndicatorFeature extends Feature
        implements TypingIndicatorReceivedHook.TypingIndicatorReceivedListener {

    public TaskerEventTypingIndicatorFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.TASKER_TYPING_INDICATOR_RECEIVED;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.TASKER_EVENT;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.TYPING_INDICATOR_RECEIVE };
    }

    @Override
    public void onTypingIndicatorReceived(long userKey, long threadKey, boolean isTyping) {
        ActivityOnUserTypingConfig.triggerMessageReceived(gateway.getContext(), isTyping, userKey, threadKey);
    }
}
