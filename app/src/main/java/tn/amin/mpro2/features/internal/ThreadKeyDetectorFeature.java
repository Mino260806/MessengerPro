package tn.amin.mpro2.features.internal;

import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.ConversationEnterHook;
import tn.amin.mpro2.hook.all.ConversationLeaveHook;
import tn.amin.mpro2.orca.OrcaGateway;

public class ThreadKeyDetectorFeature extends Feature
        implements ConversationEnterHook.ConversationEnterListener,
        ConversationLeaveHook.ConversationLeaveListener {
    public ThreadKeyDetectorFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.INTERNAL_THREADKEY_DETECTOR;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.INTERNAL;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.CONVERSATION_ENTER, HookId.CONVERSATION_LEAVE };
    }

    @Override
    public void onConversationEnter(Long threadKey) {
        gateway.currentThreadKey = threadKey;
    }

    @Override
    public void onConversationLeave() {
        gateway.currentThreadKey = null;
    }
}
