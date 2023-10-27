package tn.amin.mpro2.hook;

import tn.amin.mpro2.hook.all.CameraLaunchHook;
import tn.amin.mpro2.hook.all.ConversationEnterHook;
import tn.amin.mpro2.hook.all.ConversationLeaveHook;
import tn.amin.mpro2.hook.all.MessageReceivedHook;
import tn.amin.mpro2.hook.all.MessageSentHook;
import tn.amin.mpro2.hook.all.MediaTranscoderHook;
import tn.amin.mpro2.hook.all.MessagesDisplayHook;
import tn.amin.mpro2.hook.all.SeenIndicatorHook;
import tn.amin.mpro2.hook.all.ThreadAttrsHook;
import tn.amin.mpro2.hook.all.TypingIndicatorReceivedHook;
import tn.amin.mpro2.hook.all.TypingIndicatorSentHook;
import tn.amin.mpro2.hook.all.UIColorsHook;
import tn.amin.mpro2.orca.OrcaGateway;

public class MProHookManager extends HookManager {
    public MProHookManager(OrcaGateway gateway) {
        initHooks();
    }

    private void initHooks() {
        addHook(new ConversationEnterHook());
        addHook(new ConversationLeaveHook());
        addHook(new SeenIndicatorHook());
        addHook(new MessageSentHook());
        addHook(new MessageReceivedHook());
        addHook(new TypingIndicatorSentHook());
        addHook(new TypingIndicatorReceivedHook());
        addHook(new CameraLaunchHook());
        addHook(new MediaTranscoderHook());
        addHook(new MessagesDisplayHook());
        addHook(new UIColorsHook());
        addHook(new ThreadAttrsHook());
    }
}
