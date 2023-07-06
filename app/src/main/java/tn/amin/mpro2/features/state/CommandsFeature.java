package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.features.util.message.command.CommandsManager;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.messaging.OrcaMessageSender;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.TextMessage;

public class CommandsFeature extends Feature
        implements MessageSentHook.MessageSentListener {
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
        return new HookId[] { HookId.MESSAGE_SEND};
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

    public boolean isCommandsInputEnabled() {
        return gateway.pref.sp.getBoolean("mpro_commands_send_input", true);
    }

    @Override
    public HookListenerResult<TextMessage> onMessageSent(TextMessage message, Long threadKey) {
        String content = message.content;

        if (!isEnabled() || !content.startsWith("/")) return HookListenerResult.ignore();

        boolean success = mCommandsManager.execute(content, new OrcaMessageSender(gateway.mailboxConnector, threadKey));
        if (success) {
            if (isCommandsInputEnabled()) {
                return HookListenerResult.consume(message);
            } else {
                return HookListenerResult.consume(null);
            }
        } else {
            return HookListenerResult.ignore();
        }
    }
}
