package tn.amin.mpro2.features.action;

import java.util.Objects;

import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.hook.all.MessagesDisplayHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.TextMessage;
import tn.amin.mpro2.orca.wrapper.MessageWrapper;

public class TranslationFeature extends Feature
        implements MessagesDisplayHook.MessageDisplayHookListener, MessageSentHook.MessageSentListener {
    public TranslationFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.TRANSLATE;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.ACTION;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.MESSAGES_DISPLAY, HookId.MESSAGE_SEND };
    }

    @Override
    public void executeAction() {
        // TODO ask for language when pressed
    }


    @Override
    public HookListenerResult<TextMessage> onMessageSent(TextMessage message, Long threadKey) {
        return HookListenerResult.ignore();
    }

    @Override
    public void onMessageDisplay(MessageWrapper message, int index, int count) {
        if (!Objects.equals(message.getUserKey().getUserKeyLong(), gateway.authData.getFacebookUserKey())) {
            message.setText(message.getText() + "\nModified by MPro");
        }
    }
}
