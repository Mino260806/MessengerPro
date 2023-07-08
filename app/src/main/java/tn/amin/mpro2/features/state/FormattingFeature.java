package tn.amin.mpro2.features.state;

import androidx.annotation.Nullable;

import tn.amin.mpro2.R;
import tn.amin.mpro2.features.Feature;
import tn.amin.mpro2.features.FeatureId;
import tn.amin.mpro2.features.FeatureType;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.hook.listener.HookListenerResult;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.datatype.TextMessage;
import tn.amin.mpro2.ui.toolbar.ToolbarButtonCategory;

public class FormattingFeature extends Feature
        implements MessageSentHook.MessageSentListener {
    @Nullable
    @Override
    public String getPreferenceKey() {
        return "mpro_text_format";
    }

    public FormattingFeature(OrcaGateway gateway) {
        super(gateway);
    }

    @Override
    public FeatureId getId() {
        return FeatureId.FORMATTING;
    }

    @Override
    public FeatureType getType() {
        return FeatureType.CHECKABLE_STATE;
    }

    @Override
    public HookId[] getHookIds() {
        return new HookId[] { HookId.MESSAGE_SEND};
    }

    @Override
    public boolean isEnabledByDefault() {
        return true;
    }

    @Nullable
    @Override
    public ToolbarButtonCategory getToolbarCategory() {
        return ToolbarButtonCategory.TOGGLABLE;
    }

    @Nullable
    @Override
    public Integer getToolbarDescription() {
        return R.string.feature_message_formatting;
    }

    @Nullable
    @Override
    public Integer getDrawableResource() {
        return R.drawable.ic_toolbar_formatting;
    }

    @Override
    public HookListenerResult<TextMessage> onMessageSent(TextMessage message, Long threadKey) {
        if (!isEnabled()) return HookListenerResult.ignore();

        TextMessage textMessage = (TextMessage) gateway.getMessageParser().parse(message.content, threadKey, false).get(0);
        if (textMessage.content.equals(message.content)) {
            textMessage.mentions = message.mentions;
        }
        textMessage.replyMessageId = message.replyMessageId;
        return HookListenerResult.consume(textMessage);
    }
}
