package tn.amin.mpro2.features.tasker;

import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.MessageReceivedHook;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.tasker.ActivityOnMessageConfig;

public class TaskerEventMessageFeature extends Feature
        implements MessageReceivedHook.MessageReceivedListener {

    public TaskerEventMessageFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.TASKER_MESSAGE_RECEIVED;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.TASKER_EVENT;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.MESSAGE_RECEIVE };
    }

    @Override
    public void onMessageReceived(String message, String messageId, String senderUserKey, long convThreadKey) {
        // TODO this doesn't work in muted conversations
        ActivityOnMessageConfig.triggerMessageReceived(gateway.getContext(), message, messageId, senderUserKey, convThreadKey);
    }
}
