package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.debug.Logger;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.message.command.CommandsManager;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.MessageReceivedHook;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.messaging.OrcaMessageSender;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.TextMessage;

public class CommandsFeature extends Feature
        implements MessageSentHook.MessageSentListener, MessageReceivedHook.MessageReceivedListener {
    private final CommandsManager mCommandsManager;

    public CommandsFeature(OrcaGateway gateway) {
        super(gateway);

        mCommandsManager = new CommandsManager(gateway);
    }

    @Override
    public FeatureId getId() {
        return null;
    }

    @Override
    public FeatureType getType() {
        return null;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.MESSAGE_SEND, HookId.MESSAGE_RECEIVE };
    }

    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_commands";
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Override
    public HookListenerResult<TextMessage> onMessageSent(TextMessage message, Long threadKey) {
        String content = message.content;

        if (!isEnabled() || !content.startsWith("/")) return HookListenerResult.ignore();

        boolean success = mCommandsManager.execute(content, new OrcaMessageSender(gateway.mailboxConnector, threadKey));
        if (success) {
            if (gateway.pref.isCommandsInputEnabled()) {
                return HookListenerResult.consume(message);
            } else {
                return HookListenerResult.consume(null);
            }
        } else {
            return HookListenerResult.ignore();
        }
    }

    @Override
    public void onMessageReceived(String content, String messageId, long senderUserKey, long convThreadKey) {
        String allowOtherStatus = gateway.pref.getCommandsAllowOther();
        if (!isEnabled() || !content.startsWith("/") || allowOtherStatus.equals("never")) return;

        if (allowOtherStatus.equals("always") || (allowOtherStatus.equals("when_inside")
                && gateway.requireThreadKey(false))) {
            boolean success = mCommandsManager.execute(content, new OrcaMessageSender(gateway.mailboxConnector, convThreadKey, messageId));
        }
    }
}
