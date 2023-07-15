package tn.amin.mpro2.hook.all;

import android.os.Parcelable;

import java.util.Set;

import de.robv.android.xposed.XC_MethodHook;
import tn.amin.mpro2.hook.BaseHook;
import tn.amin.mpro2.hook.HookId;
import tn.amin.mpro2.hook.helper.ContextHookHelper;
import tn.amin.mpro2.orca.OrcaGateway;
import tn.amin.mpro2.orca.wrapper.ThreadKeyWrapper;
import tn.amin.mpro2.orca.wrapper.UserKeyWrapper;

public class TypingIndicatorReceivedHook extends BaseHook {
    private final static String ACTION_TYPING_INDICATOR = "com.facebook.presence.ACTION_OTHER_USER_TYPING_CHANGED";
//    private final static String ACTION_MESSAGE_RECEIVED = "com.facebook.presence.ACTION_PUSH_RECEIVED";

    @Override
    public HookId getId() {
        return HookId.TYPING_INDICATOR_RECEIVE;
    }

    @Override
    protected Set<XC_MethodHook.Unhook> injectInternal(OrcaGateway gateway) {
        return ContextHookHelper.interceptBroadcast(ACTION_TYPING_INDICATOR, wrap(intent -> {
            Long userKey = -1L, threadKey = -1L;
            boolean isTyping = false;

            int state = intent.getIntExtra("extra_new_state", -1);
            Parcelable userKeyObject = intent.getParcelableExtra("extra_user_key");
            Parcelable threadKeyObject = intent.getParcelableExtra("extra_thread_key");

            UserKeyWrapper userKeyWrapper = new UserKeyWrapper(userKeyObject);
            userKey = userKeyWrapper.getUserKeyLong();

            if (threadKeyObject != null) {
                ThreadKeyWrapper threadKeyWrapper = new ThreadKeyWrapper(threadKeyObject);
                if ("GROUP".equals(threadKeyWrapper.getType())) {
                    threadKey = threadKeyWrapper.getGroupThreadKey();
                }
            }
            if (threadKey == -1L) {
                threadKey = userKey;
            }

            isTyping = state == 1;

            final Long finalUserKey = userKey;
            final Long finalThreadKey = threadKey;
            final boolean finalIsTyping = isTyping;
            notifyListeners((listener) -> ((TypingIndicatorReceivedListener) listener).onTypingIndicatorReceived(finalUserKey, finalThreadKey, finalIsTyping));

            return false;
        }), this::wrapIgnoreWorking);

//                   TODO use this for messages history
//
//                    Object messageObject = intent.getParcelableExtra("extra_message");
//
//                    Logger.info("Received a new message ! " + messageObject);
//
//                    if (messageObject != null) {
//                        MessageWrapper message = new MessageWrapper(gateway, messageObject);
//                        Logger.logObjectRecursive(message);
//                    }
//                }
    }

    public interface TypingIndicatorReceivedListener {
        void onTypingIndicatorReceived(long userKey, long threadKey, boolean isTyping);
    }
}
